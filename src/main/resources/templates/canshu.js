/**
 * Created by cyh on 2017/6/29.
 */


var setUp = {
    baseUrl: "http://localhost:8011",                     // 项目根目录
    uploadFunc:'/upload',                                 //上传函数
    fileSizeLimit:2000 * 1024 * 1024,                     //文件总大小
    fileSingleSizeLimit:4 * 500 * 1024 * 1024,            //文件单个大小
    chunked:true,                                        //是否分块
    chunkSize:5 * 1024 * 1024,                            //分块大小
    accept:{
        extensions: 'mp4,gif,jpg,jpeg,bmp,png'           //允许上传的文件后缀名
    },
    fileInfo: function(fileInfo){                       //获取文件信息的处理函数

    },

   progress: function(){                               //进度条上传

   },
    uploadError:function(){                           //上传出错
        alert("上传失败！ 请重试！");
    },
    beforeSendFile:function(file){                       //所有分块上传前调用此函数
        $(".cyh-fileName").text(file.name);
    },
    md5Progress:function(percentage){                //文件MD5转码的过程
        $('#item1').find("p.state").text("正在读取文件信息...");
    },
    afterMd5:function(){                            //Md5完成后
        $('#item1').find("p.state").text("成功获取文件信息...");
    },
    beforeSend:function(block){                          //每个分块上传前调用此函数

    },
    onMerge:function(){                                //正在合并
        $("#progress-bar").text("合并中...");
    },
    afterSendFile:function(){                      //所有分块上传完毕后调用此函数
        $("#progress-bar").text("文件上传成功...");
        $('#item1').find("p.state").text("文件上传成功...");
        $("#progress").fadeOut(2000);         //动画 变透明
        $("#progress-bar").css("width", 2 + '%');
    },
    beforeSendFileSkip:function(res){            //文件ajax提交验证存在
        $('#item1').find("p.state").text("文件重复，已跳过");
    },
    fileQueued:function(file){                        //文件加入队列

    },
    uploadProgress:function(percentage){          //文件上传进度

    },
    all:function(type){                           //上传过程中所有的触发类型
        console.log(type);
    }
};