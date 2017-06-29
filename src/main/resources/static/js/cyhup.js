/**
 * Created by cyh on 2017/6/29.
 */

var cyhup = function (setUp) {

    var BASE_URL = setUp.baseUrl;
    var UPLOADFUNC = setUp.uploadFunc;

    var fileMd5;
    var suffix;
    var fileExist = false;       //文件存在全局变量
    var numChunks;
    var me;
    var fileInfo = {};            //文件信息


    function setFileInfo(file) {    //设置文件信息
        fileInfo.fileName = file.name;
        fileInfo.fileSize = file.size;
        fileInfo.contentType = file.type;
        fileInfo.extensionName = file.ext;
        !!(setUp.fileInfo) && setUp.fileInfo(fileInfo);
    }

    function cancel(uploader, file, callback) {
        if (!(!!$(".cyh-cancel").data("events") && !!$(".cyh-cancel").data("events")["click"])) {
            $(".cyh-cancel").click(function () {
                $.ajax({
                    type: "POST",
                    url: BASE_URL + UPLOADFUNC,
                    data: {
                        file: fileMd5,                         //文件唯一标记
                        cancel: true,                         //文件后缀名
                    },
                    async: true,                                //注意，这里是同步请求，也就是请求完毕后才执行$.ajax后面的函数
                    dataType: "json",
                    success: function (response) {
                        uploader.reset();                      //重置上传器
                        callback();
                    }
                });
            });
        }
    }

    function pause(uploader, file) {
        if (!(!!$(".cyh-pause").data("events") && !!$(".cyh-pause").data("events")["click"])) {
            $(".cyh-pause").click(
                function () {
                    if ($(this).attr("data-bind") == "pause") {
                        $(this).text("继续");
                        $(this).attr("data-bind", "continue");
                        me = uploader.stop();
                    }
                    else if (
                        $(this).attr("data-bind") == "continue") {
                        $(this).text("暂停");
                        $(this).attr("data-bind", "pause");
                        uploader.upload();
                    }
                });
        }
    }

    function binarySearch(arr, key) {                 //二分搜索查找分块
        var low = 0,
            high = arr.length - 1,
            mid = Math.floor((low + high) / 2);
        while (low <= high) {
            mid = Math.floor((low + high) / 2);
            if (key == arr[mid]) {
                return mid;
            } else if (key < arr[mid]) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

  //分片为真才开启断点续传，分片关闭不支持断点续传

        WebUploader.Uploader.register({           //监听分块上传过程中的三个时间点
            "before-send-file": "beforeSendFile",    //上传单个文件前触发，用于向服务器发送请求验证MD5;即文件是否存在
            "before-send": "beforeSend",              //上传分块前触发，用于向服务器发送请求验证分块在服务器中是否存在
            "after-send-file": "afterSendFile",
        }, {
            //时间点1：所有分块进行上传之前调用此函数
            beforeSendFile: function (file) {
                !!(setUp.beforeSendFile) && setUp.beforeSendFile(file);       //上传前处理
                suffix = file.ext;
                var deferred = WebUploader.Deferred(),
                    owner = this.owner;
                setFileInfo(file);              //设置文件信息
                owner.md5File(file, 0, 10 * 1024 * 1024)  //计算文件的唯一标记，用于断点续传
                    .progress(function (percentage) {
                        !!(setUp.md5Progress) && setUp.md5Progress(percentage);
                    })
                    .fail(function () {       //如果读取出错了，则通过reject告诉webuploader文件上传出错。
                        deferred.reject();
                    })
                    .then(function (val) {
                        fileMd5 = val;
                        $.ajax({
                            type: "POST",
                            url: BASE_URL + UPLOADFUNC,
                            data: {
                                fileMd5: fileMd5,                         //文件唯一标记
                                suffix: suffix,                           //文件后缀名
                                fileSize: fileInfo.fileSize,                            //整个文件的信息
                            },
                            async: true,                                //注意，这里是同步请求，也就是请求完毕后才执行$.ajax后面的函数
                            dataType: "json",
                            success: function (response) {
                                if (response.exist.fileExist) {    //文件存在，设置文件存在全局变量，避免继续发送分块信息
                                    fileExist = true;
                                    owner.skipFile(file);              //跳过文件上传
                                    !!(setUp.beforeSendFileSkip) && setUp.beforeSendFileSkip(response);
                                }
                                numChunks = response.chunks;
                                console.log(file);
                                pause(owner, file);    //暂停
                                cancel(owner, file, function () {
                                    alert("取消成功！");
                                });      //取消文件

                                deferred.resolve();    // 介绍此promise, webuploader接着往下走。
                            }
                        });

                        !!(setUp.afterMd5) && setUp.afterMd5();

                    });
                return deferred.promise();
            },

            beforeSend: function (block) {                         //如果有分块上传，则每个分块上传之前调用此函数
                var deferred = WebUploader.Deferred();          //规定返回一个deffer对象，实现异步调用
                if (!fileExist) {                              //文件不存在才去请求分块是否存在，若文件存在，直接秒传
                    if (numChunks != null) {
                        if (binarySearch(numChunks, block.chunk) != -1)
                            deferred.reject();         //分块存在跳过
                        else
                            deferred.resolve();
                    } else
                        deferred.resolve();

                } else
                    deferred.reject();   //文件存在
                this.owner.options.formData.fileMd5 = fileMd5;   //上传的文件的Md5标识码
                this.owner.options.formData.suffix = suffix;
                this.owner.options.formData.fileSize = fileInfo.fileSize;
                !!(setUp.beforeSend) && setUp.beforeSend(block);
                return deferred.promise();
            },
            //时间点3：所有分块上传成功后调用此函数
            afterSendFile: function () {

                if (!fileExist) {
                    //如果分块上传成功，则通知后台合并分块
                    $.ajax({
                        type: "POST",
                        url: BASE_URL + UPLOADFUNC,
                        data: {
                            fileMd5: fileMd5,
                            suffix: suffix,
                            fileSize: fileInfo.fileSize,
                            merge: true
                        },
                        success: function (response) {
                            !!(setUp.afterSendFile) && setUp.afterSendFile();
                        }
                    });
                    !!(setUp.onMerge) && setUp.onMerge();

                } else {
                    !!(setUp.afterSendFile) && setUp.afterSendFile();
                }
                fileExist = false;                          //一个文件上传完毕后置文件不存在标志位false
                this.owner.reset();                         //重置队列
            }
        });


    var param = {
        auto: true,                                       //选中直接上传
        swf: BASE_URL + '/swf/Uploader.swf',
        server: BASE_URL + UPLOADFUNC,                    // 文件接收服务端
        fileVal: 'fileToUpload',                          //上传的文件name
        pick: '#filePicker',
        accept: !!(setUp.accept) ? setUp.accept:null,
        chunked: !!(setUp.chunked),                                   //是否分片
        chunkSize: !!(setUp.chunkSize) ? setUp.chunkSize: 524800,
        chunkRetry: 2,                                   //允许重传
        threads: 2,
        duplicate: false,                               //不能有相同文件存在
        fileNumLimit: 300,
        fileSizeLimit: setUp.fileSizeLimit,               // 2000 M
        fileSingleSizeLimit: setUp.fileSingleSizeLimit,      // 2000 M
        resize: false                            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
    };

    var uploader = WebUploader.create(param);

    uploader.on('all', function (type) {
        !!(setUp.all) && setUp.all(type);
    });
    uploader.on('uploadError', function (file) {
        file.setStatus(WebUploader.File.Status.ERROR);   //上传出错
        $(".cyh-pause").trigger("click");
        !!(setUp.uploadError) && setUp.uploadError();

    });
    uploader.on('fileQueued', function (file) {                    //文件加入队列进行的操作
        !!(setUp.fileQueued) && setUp.fileQueued(file);
    });

    uploader.on('uploadProgress', function (file, percentage){    // 文件上传过程中创建进度条实时显示
        !!(setUp.uploadProgress) && setUp.uploadProgress(file,percentage);
    });

    uploader.on('uploadSuccess',function(file){    //不分块上传则向后台发送tmp转正式文件的信号
        // if(!setUp.chunked){
        //     $.ajax({
        //         type: "POST",
        //         url: BASE_URL + UPLOADFUNC,
        //         data: {
        //             fileMd5: fileMd5,
        //             suffix: suffix,
        //             fileSize: fileInfo.fileSize,
        //             merge: true
        //         },
        //         success: function (response) {
        //             !!(setUp.afterSendFile) && setUp.afterSendFile();
        //         }
        //     });
        // }
    });

    uploader.on("uploadStart",function(file){    //单个文件开始上传前触发

    });

    uploader.on("uploadFinished",function(file){   //所有文件都上传结束后触发

    });


}
