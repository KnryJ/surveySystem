function render(survey) {
  document.querySelector('#suid').value = survey.suid
  document.querySelector('#title').textContent = survey.title

  let section = document.querySelector('#questionList')
  for (let i in survey.questionList) {
    let q = survey.questionList[i]

    // <label><input type="checkbox" name="bind-quid" value="1">您的收入是多少1</label>
    let checkbox = document.createElement('input')
    checkbox.type = 'checkbox'
    checkbox.name = 'bind-quid'
    checkbox.checked = q.isBounden
    checkbox.value = q.quid

    let label = document.createElement('label')
    label.appendChild(checkbox)
    label.appendChild(document.createTextNode(q.question))
    section.appendChild(label)
  }
}

window.onload = function () {
  let suid = getParam("suid")
  let xhr = new XMLHttpRequest()
  xhr.open('get', '/survey/bind.json?suid=' + suid)
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    renderCurrentUser(data.currentUser)
    if (data.data) {
      render(data.data)
    }
  }
  xhr.send()
}