export function MP(ak) {
    return new Promise(function (resolve, reject) {
        if (window.BMap) {
            resolve(BMap);
            return;
        }
        var script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "http://api.map.baidu.com/api?v=2.0&ak=" + ak + "&callback=init";
        script.onerror = reject;
        if (script.readyState) { //IE
            console.log(1);
            script.onreadystatechange = function () {　　　　　　　　
                if (script.readyState == 'complete' || script.readyState == 'loaded') {　　　　　　　　　　
                    script.onreadystatechange = null;　　　　　　　　　　
                    resolve(BMap);　　　　　
                }　　　　　　
            }　　　　
        } else { //非IE
            script.onload = function () {
                resolve(BMap);
            }　　　　
        }
        document.head.appendChild(script);
    })
}