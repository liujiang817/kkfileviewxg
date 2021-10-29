<!DOCTYPE HTML>
<html>
<head>
<title>${file.name}文件预览</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
<#include "*/commonHeader.ftl">
<script src="js/svg-pan-zoom.js"></script>
<#if currentUrl?contains("http://") || currentUrl?contains("https://")>
    <#assign finalUrl="${currentUrl}">
<#else>
    <#assign finalUrl="${baseUrl}${currentUrl}">
</#if>
</head>
<body>
<div id="container">
</div>
<script type="text/javascript">
      $(function(){
	    var url = '${finalUrl}';
    var baseUrl = '${baseUrl}'.endsWith('/') ? '${baseUrl}' : '${baseUrl}' + '/';
    if (!url.startsWith(baseUrl)) {
        url = baseUrl + 'getCorsFile?urlPath=' + encodeURIComponent(url);
    }
	 
	  var lastEventListener = null;
	  var gaodu1 =$(document).height();
	  var gaodu=gaodu1-5; 
        function createNewEmbed(src){
          var embed = document.createElement('embed');
          embed.setAttribute('style', 'width: 99%; height: '+gaodu+'px; border:1px solid black;');
          embed.setAttribute('type', 'image/svg+xml');
          embed.setAttribute('src', src);

          document.getElementById('container').appendChild(embed)

         lastEventListener = function(){
            svgPanZoom(embed, {
              zoomEnabled: true,
              controlIconsEnabled: true
            });
          }
          embed.addEventListener('load', lastEventListener)
          return embed;
        }
        var lastEmbedSrc = url, lastEmbed = createNewEmbed(lastEmbedSrc);
     
});
   
    /*初始化水印*/
   if (!!window.ActiveXObject || "ActiveXObject" in window)
{
}else{
 initWaterMark();
}
</script>
</body>
</html>