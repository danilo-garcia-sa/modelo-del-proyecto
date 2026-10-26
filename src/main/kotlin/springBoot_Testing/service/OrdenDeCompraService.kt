package com.example.demo
data class Item(val nombre: String, val precio: Int)

class OrdenDeCompra() {
    val items = mutableListOf<Item>()
    fun agregarItem(item: Item){
    this.items.add(item)

    }
    fun calcularTotal():Int {
        return this.items.map {
            it.precio
        }.sum()
        TODO("calcular suma")
    }
}
