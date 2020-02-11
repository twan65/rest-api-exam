package com.rest.api.controller.v1;

import com.rest.api.advice.exception.CUserNotFoundException;
import com.rest.api.service.ResponseService;
import com.rest.api.entity.User;
import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.ListResult;
import com.rest.api.model.response.SingleResult;
import com.rest.api.repo.UserJpaRepo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "ログイン成功後access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "メンバーリスト照会", notes = "すべてのメンバーを照会する。")
    @GetMapping(value = "/users")
    public ListResult<User> findAllUser() {

        return responseService.getListResult(userJpaRepo.findAll());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "ログイン成功後access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "メンバー照会", notes = "メンバー番号で照会する。")
    @GetMapping(value = "/user")
    public SingleResult<User> findUserById(
            @ApiParam(value = "言語", defaultValue = "ja") @RequestParam String lang) {
        // SecurityContextから認証ができたメンバーの情報を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        // 結果が1件の場合getSingleResultを利用し、出力する。
        return responseService.getSingleResult(userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "ログイン成功後access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "メンバー修正", notes = "メンバー情報を修正する。")
    @PutMapping(value = "/user")
    public SingleResult<User> modify(
            @ApiParam(value = "メンバー番号", required = true) @RequestParam long msrl,
            @ApiParam(value = "メンバーID", required = true) @RequestParam String uid,
            @ApiParam(value = "メンバー名", required = true) @RequestParam String name) {
        User user = User.builder()
                 .msrl(msrl)
                 .uid(uid)
                 .name(name)
                 .build();
        return responseService.getSingleResult(userJpaRepo.save(user));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "ログイン成功後access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "メンバー削除", notes = "メンバー情報を削除する。")
    @DeleteMapping(value = "/user/{msrl}")
    public CommonResult delete(
            @ApiParam(value = "メンバー番号", required = true) @PathVariable long msrl) {
        userJpaRepo.deleteById(msrl);
        return responseService.getSuccessResult();
    }
}
