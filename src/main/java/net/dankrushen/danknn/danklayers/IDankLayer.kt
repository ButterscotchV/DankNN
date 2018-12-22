package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankNeuron

interface IDankLayer {
    val neurons: Array<DankNeuron>

    fun setBias(bias: Double)
}
