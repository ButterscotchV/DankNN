package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection

interface IDankOutputLayer : IDankLayer {
    val inputLayer: IDankInputLayer

    val inputConnections: Array<DankConnection>
}
