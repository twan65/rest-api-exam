package com.rest.api.controller.v1;

import com.rest.api.advice.exception.CEmailSigninFailedException;
import com.rest.api.advice.exception.CUserExistException;
import com.rest.api.advice.exception.CUserNotFoundException;
import com.rest.api.config.security.JwtTokenProvider;
import com.rest.api.entity.User;
import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.SingleResult;
import com.rest.api.model.social.KakaoProfile;
import com.rest.api.repo.UserJpaRepo;
import com.rest.api.service.ResponseService;
import com.rest.api.service.user.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final KakaoService kakaoService;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "ログイン", notes = "メアドでログインを行う。")
    @GetMapping(value = "/signin")
    public SingleResult<String> signin (
            @ApiParam(value = "ログインID：メアド", required = true) @RequestParam String id
            , @ApiParam(value = "パスワード", required = true) @RequestParam String password) {

        User user = userJpaRepo.findByUid(id).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CEmailSigninFailedException();
        }

        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "Signup", notes = "Signupを行う。")
    @GetMapping(value = "/signup")
    public CommonResult signup (
            @ApiParam(value = "ログインID：メアド", required = true) @RequestParam String id
            , @ApiParam(value = "パスワード", required = true) @RequestParam String password
            , @ApiParam(value = "名前", required = true) @RequestParam String name) {

        userJpaRepo.save(
                User.builder()
                .uid(id)
                .password(passwordEncoder.encode(password))
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "SNS LOGIN", notes = "SNS LOGINを行う。")
    @PostMapping(value = "/signin/{provider}")
    public SingleResult<String> signinByProvider(
            @ApiParam(value = "サービス提供provider", required = true, defaultValue = "kakao") @PathVariable String provider,
            @ApiParam(value = "SNS access_token", required = true) @RequestParam String accessToken) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        User user = userJpaRepo.findByUidAndProvider(String.valueOf(profile.getId()), provider).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "SNS SIGNUP", notes = "SIGN UPを行う")
    @PostMapping(value = "/signup/{provider}")
    public CommonResult signupProvider(@ApiParam(value = "サービス提供provider", required = true, defaultValue = "kakao") @PathVariable String provider,
                                       @ApiParam(value = "SNS access_token", required = true) @RequestParam String accessToken,
                                       @ApiParam(value = "名前", required = true) @RequestParam String name) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        Optional<User> user = userJpaRepo.findByUidAndProvider(String.valueOf(profile.getId()), provider);
        if (user.isPresent())
            throw new CUserExistException();

        User inUser = User.builder()
                .uid(String.valueOf(profile.getId()))
                .provider(provider)
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        userJpaRepo.save(inUser);
        return responseService.getSuccessResult();
    }
}
