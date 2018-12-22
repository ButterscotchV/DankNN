package net.dankrushen.danknn.danklayers

import net.dankrushen.danknn.DankConnection

interface IDankInputLayer : IDankLayer {

    var outputLayer: IDankOutputLayer

    val outputConnections: Array<DankConnection>
}
