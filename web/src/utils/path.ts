export function subPath(dir: string, sub: string) {
    if (dir.endsWith('/'))
        return dir + sub
    return dir + '/' + sub
}
