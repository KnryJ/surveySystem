function render(result) {
    document.querySelector("#title").textContent = result.title
    let resultShell = document.querySelector("#result")
    for (let i in result.questionList) {
        let r = result.questionList[i]
        let no = parseInt(i) + 1

        let chartShell = document.createElement('div')
        chartShell.id = 'quid-' + r.quid
        chartShell.style.width = '500px'
        chartShell.style.height = '500px'
        resultShell.appendChild(chartShell)
        let data = []
        for (let name in r.options) {
            let value = r.options[name]
            data.push({
                name: name,
                value: value
            });
        }

        let chart = echarts.init(chartShell)
        let option = {
            title: {
                text: no + '. ' + r.question,
                left: 'center'
            },
            series: [
                {
                    type: 'pie',
                    radius: ['40%', '70%'],
                    data: data
                }
            ]
        }
        chart.setOption(option)

        resultShell.appendChild(chartShell)
    }
}

window.onload = function () {
    let acid = getParam('acid')
    let xhr = new XMLHttpRequest();
    xhr.open('get', '/activity/detail.json?acid=' + acid)
    xhr.onload = function () {
        let data = JSON.parse(this.responseText)
        renderCurrentUser(data.currentUser)
        if (data.data) {
            render(data.data)
        }
    }
    xhr.send()
}