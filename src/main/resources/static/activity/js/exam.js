function render(exam) {
  document.querySelector('#acid').value = exam.acid
  document.querySelector('#title').textContent = exam.title
  document.querySelector('#brief').textContent = exam.brief

  let questionListShell = document.querySelector('#questionList')
  for (let i in exam.questionList) {
    let q = exam.questionList[i]
    let questionShell = document.createElement('div')
    let no = parseInt(i) + 1
    questionShell.appendChild(document.createTextNode(no + '. ' + q.question))
    let abcd = ['A', 'B', 'C', 'D']
    for (let j in q.options) {
      let option = q.options[j]
      let optionShell = document.createElement('div')
      let label = document.createElement('label')
      let input = document.createElement('input')
      input.type = 'radio'
      input.name = 'quid-' + q.quid
      input.value = abcd[j]
      label.appendChild(input);
      label.appendChild(document.createTextNode(abcd[j] + '. ' + option));

      optionShell.appendChild(label)
      questionShell.appendChild(optionShell)
    }

    questionListShell.appendChild(questionShell)
  }
}

window.onload = function () {
  let acid = getParam("acid")
  let xhr = new XMLHttpRequest()
  xhr.open('get', '/activity/exam.json?acid=' + acid)
  xhr.onload = function () {
    let data = JSON.parse(this.responseText)
    if (data.state === '未开始') {
      alert('调查还未开始，敬请期待')
      return
    }
    if (data.state === '已结束') {
      alert('调查已经结束，有机会再次参与')
      return
    }
    if (data.data) {
      render(data.data)
    }
  }
  xhr.send()
}