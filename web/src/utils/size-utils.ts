const UNITS = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']

export function toHumanSize(size: number, carry: number = 0.9 * 1024): string {
    let ui = 0
    while (size >= carry && ui < UNITS.length - 1) {
        size /= 1024
        ui++
    }
    return size.toFixed(2) + UNITS[ui]
}