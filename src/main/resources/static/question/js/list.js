function render(questionList) {
  let tbody = document.querySelector("tbody")
  for (let i in questionList) {
    let q = questionList[i]

    let tr = document.createElement('tr')
    tr.innerHTML = `<td>${q.quid}</td>`
      + `<td>${q.题目}</td>`
      + `<td>${q.选项[0]}</td>`
      + `<td>${q.选项[1]}</td>`
      + `<td>${q.选项[2]}</td>`
      + `<td>${q.选项[3]}</td>`

    tbody.appendChild(tr)
  }
}

window.onload = function () {
  let xhr = new XMLHttpRequest()
  xhr.open("get", "/question/list.json")
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    renderCurrentUser(data.currentUser);
    if (data.data) {
      render(data.data)
    }
  }
  xhr.send()
};