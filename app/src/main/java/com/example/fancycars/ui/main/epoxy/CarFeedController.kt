package com.example.fancycars.ui.main.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.example.fancycars.data.models.Car


class CarFeedController: PagedListEpoxyController<Car>() {

    var endReached = false
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildItemModel(currentPosition: Int, item: Car?): EpoxyModel<*> {
        return if (item == null)
            CarModel_()
                .id(currentPosition)
        else
            CarModel_()
                .name(item.name)
                .imageUrl(item.pictureUrl)
                .model(item.model)
                .make(item.make)
                .availability(item.availablity)
                .id(item.id)
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        super.addModels(models)
        LoadingModel_()
            .id("loading")
            .addIf(!endReached && models.isNotEmpty(), this)
    }
}