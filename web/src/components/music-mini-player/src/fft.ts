export class FFT {

    from: Uint8Array
    to: Uint8Array
    lastUpdateTime: number = 0
    updateInterval = 100
    #onUpdate: () => Uint8Array

    constructor(size: number, updateInterval: number, onUpdate: () => Uint8Array) {
        this.from = new Uint8Array(size,)
        this.to = this.from
        for (let i = 0; i < size; i++)
            this.from[i] = 128
        this.updateInterval = updateInterval
        this.#onUpdate = onUpdate
        this.to = onUpdate()
        this.lastUpdateTime = Date.now()
    }

    get size() {
        return this.from.length
    }

    get(i: number) {
        let dt = Date.now() - this.lastUpdateTime
        if (dt > this.updateInterval) {
            this.from = new Uint8Array(this.to)
            this.to = this.#onUpdate()
            this.lastUpdateTime = Date.now()
            dt %= this.updateInterval
        }
        let d = this.to[i] - this.from[i]
        let p = dt / this.updateInterval
        if (p > 1)
            p = 1
        return this.from[i] + d * p
    }

}