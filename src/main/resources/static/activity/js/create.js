function render(surveyList) {
  let select = document.querySelector('select')
  for (let i in surveyList) {
    let s = surveyList[i]

    let option = document.createElement('option')
    option.value = s.suid
    option.textContent = `${s.suid}. ${s.标题}`
    select.appendChild(option)
  }
}

window.onload = function () {
  let xhr = new XMLHttpRequest()
  xhr.open('get', '/survey/list.json')
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    renderCurrentUser(data.currentUser)
    if (data.data) {
      render(data.data)
    }
  }
  xhr.send()
};