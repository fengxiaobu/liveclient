/*
 * 页面操作（发布，播放，删除，查看发布列表，查看发布信息）
 * 编写：wangliang
 */

//封装JsonAjax方法(请求类型，url，参数，成功方法)

function JsonAjax(urltype, urlLink, paramData, sucessMethod) {
    $.ajax({
        type: urltype,
        url: urlLink,
        data: paramData,
        dataType: "json",
        success: function (data) {
            sucessMethod(data);
        },
        error: function () {
            bootbox.alert("服务器未开启或页面错误，请联系管理员");
        }
    });
}

function JsonAjaxVoid(urltype, urlLink, paramData) {
    $.ajax({
        type: urltype,
        url: urlLink,
        data: paramData,
        dataType: "json",
        error: function () {
            bootbox.alert("服务器未开启或页面错误，请联系管理员");
        }
    });
}

//增强型ajax方法：增加一个回调参数方便回调方法调用；通过回调参数，回调方法就可以在调用方法后续无法处理的业务的时候处理后续动作，比如生成列表
function EchoJsonAjax(urltype, urlLink, paramData, sucessMethod, async, index) {
    $.ajax({
        type: urltype,
        url: urlLink,
        async: async,
        data: paramData,
        dataType: "json",
        contentType: "application/json,;charset=UTF-8",
        success: function (data) {
            //把回调参数给回调方法
            sucessMethod(data, index);
        },
        error: function () {
            bootbox.alert("服务器未开启或页面错误，请联系管理员");
        }
    });
}

/*
 * --------------------发布应用事件-----------------------
 */
/**
 * 发布一个应用方法
 */
function pushAppAdd(newMethod) {
    //自定义成功方法
    var appName = $("#ad_appName").val();
    var input = $("#ad_input").val();
    var output = $("#ad_output").val();
    var ip = $("#ad_ip").val();
    var username = $("#ad_username").val();
    var password = $("#ad_password").val();
    var protoco = $("#ad_protocol").val();
    var transport = $("#ad_transport").val();
    if (isNull(appName) || isNull(input) || isNull(output)) {
        bootbox.alert("发布失败，必须填写全部参数！");
        return;
    }
    var param = $("#addForm").serializeArray();
    console.log(param);
    bootbox.confirm({
        size: "small",
        message: "确定发布名称为：’" + appName + " ‘的实时应用？（提示：发布后该实时流（在不关闭的情况下）会一直保持推送状态）",
        callback: function (result) {
            if (result) {
                if (newMethod) {
                    JsonAjax("POST", "live/push", param, newMethod);
                } else {
                    JsonAjax("POST", "live/push", param, sucessPushApp);
                }
            }
        }
    })
}

/**
 * 探测
 */
function probedevice() {
    //自定义成功方法
    var ip = $("#ad_ip").val();
    var username = $("#ad_username").val();
    var password = $("#ad_password").val();

    if (isNull(ip) || isNull(username) || isNull(password)) {
        JsonAjax("POST", "ptz/probedevice", {ip: ip, username: username, password: password}, sucessIpProbedevice);
    } else {
        JsonAjax("POST", "ptz/probedevice", {ip: ip, username: username, password: password}, sucessProbedevice);
    }

}

/**
 * 云台控制
 */
function ptzCimmand(cdnid, command) {
    JsonAjaxVoid("POST", "ptz/command", {id: cdnid, command: command, speed: 5});
}

function pushAppEdit(newMethod) {
    //自定义成功方法
    var appName = $("#ed_appName").val();
    var input = $("#ed_input").val();
    var output = $("#ed_output").val();
    var id = $("#ed_id").val();
    var ip = $("#ed_ip").val();
    var username = $("#ed_username").val();
    var password = $("#ed_password").val();
    var protoco = $("#ed_protocol").val();
    var transport = $("#ed_transport").val();
    if (isNull(appName) || isNull(input) || isNull(output)) {
        bootbox.alert("发布失败，必须填写全部参数！");
        return;
    }
    var param = $("#editForm").serializeArray();
    console.log(param);
    bootbox.confirm({
        size: "small",
        message: "确定发布名称为：’" + appName + " ‘的实时应用？（提示：发布后该实时流（在不关闭的情况下）会一直保持推送状态）",
        callback: function (result) {
            if (result) {
                if (newMethod) {
                    JsonAjax("POST", "live/edit", param, newMethod);
                } else {
                    JsonAjax("POST", "live/edit", param, sucessPushApp);
                }
            }
        }
    })
}

//是否为空
function isNull(element) {
    return element == null || String(element).trim() == "";
}

/**
 * 成功返回方法：成功发布应用后更新应用列表
 * @param element
 */
function sucessPushApp(resultData) {
    if (resultData) {
        if (resultData.status == 0) {
            //显示列表
            viewAll();
        }
        //不管是否发布成功都要显示结果信息
        bootbox.alert(resultData.msg);
    } else {
        bootbox.alert("服务器抽风了，请稍后再试！");
    }
}

function sucessProbedevice(resultData) {
    if (resultData) {
        //不管是否发布成功都要显示结果信息
        bootbox.alert(resultData.msg);
        if (resultData.code == 200) {
            //显示列表
            $("#ad_input").prop("value", resultData.result.rtspStreamUri);
        }
    } else {
        bootbox.alert("服务器抽风了，请稍后再试！");
    }
}

function sucessIpProbedevice(resultData) {
    if (resultData) {
        if (resultData.code == 200) {
            //显示列表
            bootbox.alert("发现设备  " + JSON.parse(resultData.result.ips))
        } else {
            //不管是否发布成功都要显示结果信息
            bootbox.alert(resultData.msg);
        }
    } else {
        bootbox.alert("服务器抽风了，请稍后再试！");
    }
}

/*
 * -----------------关闭应用事件---------------
 */
/**
 * 通过应用名关闭应用
 * @param element
 */
function closeApp(pushId) {
    console.log(pushId);
    bootbox.confirm({
        size: "small",
        message: "移除这个实时应用？（提示：实时应用无法暂停，停止即删除）",
        callback: function (result) {
            if (result) {
                JsonAjax("DELETE", "live/close/" + pushId, "", sucessCloseApp);
            }
        }
    })
}

function stopApp(pushId) {
    bootbox.confirm({
        size: "small",
        message: "停止这个实时应用？（提示：实时应用无法暂停，停止即删除）",
        callback: function (result) {
            if (result) {
                JsonAjax("DELETE", "live/stop/" + pushId, "", sucessCloseApp);
            }
        }
    })
}

/**
 * 成功返回方法：成功关闭应用事件
 * @param resultData
 * @param index
 */
function sucessCloseApp(resultData, index) {
    if (resultData) {
        if (resultData.status == 0) {
            //删除应用后刷新列表
            viewAll();
        }
        bootbox.alert(resultData.msg);
    } else {
        bootbox.alert("服务器抽风了 - -!");
    }
}

/*
 * ----------------------查看应用详细事件-------------------
 */
/**
 * 查看应用详细信息
 * @param element：点击事件的按钮元素
 * @param sucessMethod：成功返回的方法
 * @param index：appName，该参数如果可用，element参数将自动失效
 *
 */
function view(element, sucessMethod, index) {
    var appName = index ? index : $(element).parent().parent().data("appName");
    JsonAjax("GET", "live/view/" + appName, "", sucessMethod);
}

//成功获取应用详细（暂时不用）
function sucessvViewApp(resultData, element) {

}

/*
 * --------------列表查询事件------------
 */
/**
 * 查看当前全部应用方法
 */
function viewAll() {
    // JsonAjax("GET", "live/viewAll", "", sucessViewAllAppList);
    $('#ArbetTable').bootstrapTable('refresh');

}

/**
 * 成功返回方法：成功获取参数后把所有应用形成列表
 * @param resultData
 */
function sucessViewAllAppList(resultData) {
    if (resultData || resultData.status == 0) {
        //每次生成要先把原数据删除再生成
        $("#appList").empty();
        var listData = resultData.data;
        if (listData && listData.length > 0) {
            for (var i = 0; i < listData.length; i++) {
                var elementHTML = '<tr class="appManager"><td class="list-appName">' +
                    listData[i].appName + '</td><td class="list-input">' +
                    listData[i].input + '</td><td class="list-output">' +
                    listData[i].output + '</td><td class="list-fmt">' +
                    listData[i].ip + '</td><td class="list-ip">' +
                    listData[i].username + '</td><td class="list-username">' +
                    listData[i].password + '</td><td class="list-password">' +
                    listData[i].disableAudio + '</td><td class="playVideoOnTable">' +
                    listData[i].protocol + '</td><td class="list-protocol">' +
                    listData[i].transport + '</td><td class="list-transport">' +
                    '<input class="playVideo" type="button" value="播放" onclick="playFromList(this);"/></td><td class="closeVideoOnTable">' +
                    '<input class="closeVideo" type="button" value="关闭" onclick="closeApp(this);"/></td></tr>';
                $(elementHTML).appendTo($("#appList")).data("appName", listData[i].appName);
            }
        } else {
            bootbox.alert(resultData.msg);
            //查询列表失败清空列表
            $("#appList").empty();
        }
    } else {
        bootbox.alert("服务器抽风了 - -!");
        //查询列表失败清空列表
        $("#appList").empty();
    }
}

/*
 * -----------------发布并播放事件----------------
 */
/**
 * 发布并播放方法
 */
function pushAndPlay() {
    pushApp(sucesspushAndPlay);
}

/**
 * 成功返回方法：发布后播放器播放发布的应用
 */
function sucesspushAndPlay(resultData) {
    if (resultData) {
        if (resultData.status == 0) {
            //显示列表
            viewAll();
            var appName = resultData.data.appName;
            view(null, sucessPlayFromView, appName);
        }
        //不管是否发布成功都要显示结果信息
        bootbox.alert(resultData.msg);
    } else {
        bootbox.bootbox.alert("服务器抽风了，请稍后再试！");
    }
}

/*
 * ----------------播放事件----------------
 */
/**
 * 播放应用
 * @param element
 */
function playFromList(element) {
    bootbox.confirm({
        size: "small",
        message: "播放这个实时应用？（提示：这将会关闭正在播放的应用）",
        callback: function (result) {
            if (result) {
                view(element, sucessPlayFromView);
            }
        }
    })
}

/**
 * 成功获取应用播放地址并播放
 * @param resultData
 */
function sucessPlayFromView(resultData) {
    if (resultData) {
        if (resultData.status == 0) {
            var playUrl = resultData.data.output + resultData.data.appName;
            //修改播放地址并播放
            writeAddressAndPlay(player, playUrl)
        } else {
            bootbox.alert(resultData.msg);
        }
    } else {
        bootbox.alert("服务器抽风了 - -!");
    }
}
