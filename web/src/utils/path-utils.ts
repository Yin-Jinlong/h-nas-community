export function pathGetName(path: string): string {
    return path.split('/').pop()!
}
