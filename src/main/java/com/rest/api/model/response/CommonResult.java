package com.rest.api.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

    @ApiModelProperty(value = "レスポンスの成功確認：true/false")
    private boolean success;

    @ApiModelProperty(value = "レスポンスコード番号：>= 正常、< 非正常")
    private int code;

    @ApiModelProperty(value = "レスポンスメッセージ")
    private String msg;

}
