package com.example.fancycars.ui.main.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.example.fancycars.R
import com.example.fancycars.data.repositories.CarRepositoryImpl
import com.example.fancycars.databinding.ViewholderCarBinding


@EpoxyModelClass(layout = R.layout.viewholder_car)
abstract class CarModel: EpoxyModelWithHolder<CarModel.ViewHolder>() {

    @EpoxyAttribute
    var name = "name"
    @EpoxyAttribute
    var imageUrl = "url"
    @EpoxyAttribute
    var model = "model"
    @EpoxyAttribute
    var make = "make"
    @EpoxyAttribute
    var availability = CarRepositoryImpl.AV_IN


    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.binding.nameTV.text = name
        holder.binding.modelTV.text = model
        holder.binding.makeTV.text = make
        holder.binding.availTV.text = availability

        holder.view.let {
            Glide.with(it)
                .load(imageUrl)
                .centerCrop()
                .into(holder.binding.carIV)
        }

        holder.binding.buyBtn.visibility = if(availability == CarRepositoryImpl.AV_IN) View.VISIBLE else View.GONE
    }

    class ViewHolder: EpoxyHolder() {

        lateinit var binding: ViewholderCarBinding
        lateinit var view: View

        override fun bindView(itemView: View) {
            this.binding = ViewholderCarBinding.bind(itemView)
            this.view = itemView
        }
    }
}