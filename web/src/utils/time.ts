function to2(num: number) {
    let s = num.toString()
    return s.length < 2 ? '0' + s : s
}

export function sec2TimeStr(secs: number) {
    let s = Math.floor(secs) % 60
    let t = secs / 60
    let m = Math.floor(t % 60)
    t /= 60
    let h = Math.floor(t)
    return `${to2(h)}:${to2(m)}:${to2(s)}`
}
