package com.example.sunnyweather


class TaxiDriver{
    public fun Answer(callback: Callback):Int{
        println("去那的话要100元")
        if(callback.Consider(100)==true){
            callback.Payfor(100)
        }
        return 100
    }
}
class Passenger{
    public fun TakeTaxi(td:TaxiDriver) {
        println("师傅去华师多少钱")

        td.Answer(object :Callback{
            override fun Consider(money: Int): Boolean {
                if(money>80){
                    println(money.toString()+"太贵了80，您看80行不行")
                }
                return false
            }

            override fun Payfor(money: Int) {
                println("好的，这是你的"+money+"元")
            }
        })
    }
}
fun main(){

    val p=Passenger()
    val td=TaxiDriver()
    p.TakeTaxi(td)
}