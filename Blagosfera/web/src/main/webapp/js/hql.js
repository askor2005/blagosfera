$(document).ready(function(){
    $("#text_block").css("width", "50%");
    $("#text_block").css("height", "100%");
    $("#text_block").css("display", "inline-block");
    $("#text_block").css("vertical-align", "top");

    $("#hql_text").css("width", "100%");
    $("#hql_text").css("height", "90%");

    $("#result_block").css("width", "50%");
    $("#result_block").css("height", "90%");
    $("#result_block").css("display", "inline-block");
    $("#result_block").css("vertical-align", "top");
    $("#result_block").css("overflow", "auto");

    $("body").css("overflow", "hidden");

    $("#execute_hql").click(function(){
        $("#result_block").html("Ы");
        var interval = setInterval(function(){
            $("#result_block").html($("#result_block").html() + "ы");
        }, 50);
        $.ajax({
            url: "/hql/execute.json",
            dataType : "json",
            method: "post",
            data : {
                hql_string : $("#hql_text").val()
            },
            success : function(response) {
                clearInterval(interval);
                if (response.message != null) {
                    $("#result_block").html("Опа!<br/>" + response.message);
                } else {
                    $("#result_block").jJsonViewer(JSON.stringify(response));
                }
            },
            error : function(error) {
                clearInterval(interval);
                alert("Что то пошло совсем не так. Смотри в консоль.");
                console.log(error);
            }
        });
    })
});