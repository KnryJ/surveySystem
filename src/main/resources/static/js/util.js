function getParam(name) {
  if (location.search.length === 0) {
    return undefined;
  }

  let query = location.search.substring(1);
  let vars = query.split('&');
  for (let i in vars) {
    let pair = vars[i].split('=');
    if (pair[0] === name) {
      return pair[1];
    }
  }

  return undefined;
}