package com.widget.listenerscontainer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel : ViewModel(), IChanges {
    private val _container = MutableStateFlow<List<DataWrapper>>(emptyList())
    val container: StateFlow<List<DataWrapper>> = _container

    private var _isStarted = MutableStateFlow(false)
    var isStarted: StateFlow<Boolean> = _isStarted

    fun setStart(value: Boolean) {
        _isStarted.value = value
    }

    fun append(element : DataWrapper) {
        val currentList = _container.value.toMutableList()
        currentList.add(element)
        _container.value = currentList.toList()
    }

    fun delete(element : DataWrapper) {
        val idx = getElementByUuid(element.uuid)
        if (idx == -1) {
            return
        }
        val currentList = _container.value.toMutableList()

        println("- delete->${currentList.size}")

        currentList.removeAt(idx)

        _container.value = currentList.toList()

        println("+ delete->${currentList.size}")

    }

    fun size() : Int {
        return _container.value.toMutableList().size
    }

//    override fun setValue(index: Int, value: Int) {
//        val currentList = _counter.value.toMutableList()
//    //  Check if index is within the bounds of the list
//        if (index >= 0 && index < currentList.size) {
//            currentList[index] = value
//            _counter.value = currentList.toList()
//        } else {
//            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size ${currentList.size}")
//        }
//    }

    override fun setValue(index: Int, value: DataWrapper) {
        val currentList = _container.value.toMutableList()
        if (index >= currentList.size) {
            val list = resizeList(currentList.toList(), index + 1)
            _container.value = list
        }
        _container.value = _container.value.toMutableList().apply {
            // Check if index is within the bounds of the list
            if (index in indices) {
                this[index] = value
            } else {
                throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
            }
        }
    }

    fun getElementByIndex(index: Int) : DataWrapper {
        val currentList = _container.value.toMutableList()
        if (index >= 0 && index < currentList.size) {
            return currentList[index]
        }
        println("getElementByIndex->return new DataWrapper")
        return DataWrapper()
    }

    private fun getElementByUuid(uuid: String) : Int {
        var idx = -1
        val currentList = _container.value.toMutableList()
        currentList.forEach{
            idx++
            if (uuid == it.uuid) {
                 return idx
            }
        }
        return idx
    }
}

fun resizeList(list: List<DataWrapper>, newSize: Int): List<DataWrapper> {
    return if (newSize <= list.size) {
        list
    } else {
        // If the new size is larger, pad the list with nulls or any default value
        val newList = list.toMutableList()
        repeat(newSize - list.size) {
            newList.add(DataWrapper()) // You can change this to null or any default value
        }
        newList
    }
}
