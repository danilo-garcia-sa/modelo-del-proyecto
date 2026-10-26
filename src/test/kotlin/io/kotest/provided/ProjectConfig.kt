package com.example.demo

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class ProjectConfig : AbstractProjectConfig() {
	override val extensions = listOf(SpringExtension())
}
