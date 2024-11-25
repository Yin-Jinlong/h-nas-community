export function subPath(dir: string | string[], sub: string) {
    if (Array.isArray(dir))
        dir = dir.join('/')
    if (dir.endsWith('/'))
        return dir + sub
    return dir + '/' + sub
}
