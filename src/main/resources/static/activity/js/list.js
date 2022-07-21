function render(activityList) {
  let tbody = document.querySelector("tbody")
  for (let i in activityList) {
    let a = activityList[i]

    let tr = document.createElement('tr')
    let state;
    if (a.state === '已结束') {
      state = `<a href="${location.origin}/activity/detail.html?acid=${a.acid}">${a.state}</a>`
    } else {
      state = a.state
    }

    let link = `${location.origin}/activity/exam.html?acid=${a.acid}`
    tr.innerHTML = `<td>${a.acid}</td>`
      + `<td>${a.title}</td>`
      + `<td>${a.startedAt}</td>`
      + `<td>${a.endedAt}</td>`
      + `<td>${state}</td>`
      + `<td><a href="${link}">${link}</a></td>`

    tbody.appendChild(tr)
  }
}

window.onload = function () {
  let xhr = new XMLHttpRequest();
  xhr.open("get", "/activity/list.json")
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    renderCurrentUser(data.currentUser)
    if (data.data) {
      render(data.data)
    }
  };
  xhr.send()
}