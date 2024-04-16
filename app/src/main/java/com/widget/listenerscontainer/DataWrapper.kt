package com.widget.listenerscontainer

import java.util.UUID

data class DataWrapper(var counter: Int = 0, var uuid: String = UUID.randomUUID().toString())
