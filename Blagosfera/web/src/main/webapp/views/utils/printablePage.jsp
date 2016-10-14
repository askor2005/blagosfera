<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<!-- <input type="button" onclick="printDiv('printableArea')" value="Печать"/> -->
<div id="printableArea">
    ${text}
</div>
<script type="text/javascript">
    function printDiv(divName) {
        var printContents = document.getElementById(divName).innerHTML;
        var originalContents = document.body.innerHTML;
        document.body.innerHTML = printContents;
        window.print();
        document.body.innerHTML = originalContents;
    }
    window.onload = function() {
        printDiv('printableArea');
    }
</script>
</body>
</html>
