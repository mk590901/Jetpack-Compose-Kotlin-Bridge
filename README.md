# Jetpack Compose 🔄 Kotlin interaction

_The repository contains an application that implements a bridge between @Composable code and Kotlin objects that implement business logic without any direct connection with the GUI_

## Introduction

The application allows to dynamically create (and, if need, delete) a group of UI's elements as cards, which reflect the data changes by a third-party process via _ChangesAdapter_. The main element of the application is the _AppViewModel_ object derived from _ViewModel()_, which is encapsulated as a listener inside _ChangesAdapter_.

## Movie

https://github.com/mk590901/Jetpack-Compose-Kotlin-Bridge/assets/125393245/2cca2360-24e5-464b-827c-92513db25ec7


