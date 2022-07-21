function render(surveyList) {
  let tbody = document.querySelector("tbody")
  for (let i in surveyList) {
    let s = surveyList[i]

    let tr = document.createElement('tr')
    tr.innerHTML = `<td>${s.suid}</td>`
      + `<td><a href="/survey/bind.html?suid=${s.suid}">${s.标题}</a></td>`
      + `<td>${s.简介}</td>`

    tbody.appendChild(tr)
  }
}

window.onload = function () {
  let xhr = new XMLHttpRequest()
  xhr.open("get", "/survey/list.json")
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    renderCurrentUser(data.currentUser)
    if (data.data) {
      render(data.data)
    }
  }
  xhr.send()
}