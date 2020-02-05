package com.rest.api.controller.v1;

import com.rest.api.service.ResponseService;
import com.rest.api.entity.User;
import com.rest.api.model.response.CommonResult;
import com.rest.api.model.response.ListResult;
import com.rest.api.model.response.SingleResult;
import com.rest.api.repo.UserJpaRepo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;

    @ApiOperation(value = "メンバーリスト照会", notes = "すべてのメンバーを照会する。")
    @GetMapping(value = "/user")
    public ListResult<User> findAllUser() {
        return responseService.getListResult(userJpaRepo.findAll());
    }

    @ApiOperation(value = "メンバー照会", notes = "メンバー番号で照会する。")
    @GetMapping(value = "/user/{msrl}")
    public SingleResult<User> findUserById(
            @ApiParam(value = "userId", required = true) @PathVariable long msrl) {
        // 結果データが１件の場合、getBasicResultを利用し、結果を出力する。
        return responseService.getSingleResult(userJpaRepo.findById(msrl).orElse(null));
    }

    @ApiOperation(value = "メンバー入力", notes = "メンバーを入力する。")
    @PostMapping(value = "/user")
    public SingleResult<User> save(@ApiParam(value = "メンバーID", required = true) @RequestParam String uid,
                                   @ApiParam(value = "メンバー名", required = true) @RequestParam String name) {
        User user = User.builder()
                .uid(uid)
                .name(name)
                .build();
        return responseService.getSingleResult(userJpaRepo.save(user));
    }

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

    @ApiOperation(value = "メンバー削除", notes = "メンバー情報を削除する。")
    @DeleteMapping(value = "/user/{msrl}")
    public CommonResult delete(
            @ApiParam(value = "メンバー番号", required = true) @PathVariable long msrl) {
        userJpaRepo.deleteById(msrl);
        return responseService.getSuccessResult();
    }
}
