<template>
  <div class="order-train">
    <span class="order-train-main">{{dailyTrainTicket.date}}</span>&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.trainCode}}</span>次&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.start}}</span>站
    <span class="order-train-main">({{dailyTrainTicket.startTime}})</span>&nbsp;
    <span class="order-train-main">——</span>&nbsp;
    <span class="order-train-main">{{dailyTrainTicket.end}}</span>站
    <span class="order-train-main">({{dailyTrainTicket.endTime}})</span>&nbsp;
    <br><br>
    <div class="order-train-ticket">
      <span v-for="item in seatTypes" :key="item.type">
        <span>{{item.desc}}</span>：
        <span class="order-train-ticket-main">{{item.price}}￥</span>
        <span class="order-train-ticket-main">{{item.count}}</span> 张票
      </span>
    </div>
  </div>
  <a-divider/>
  <b>勾选要购票的乘客：</b>&nbsp;
  <a-checkbox-group v-model:value="passengerChecks" :options="passengerOptions" />
  <br/>
  选中的乘客：{{passengerChecks}}
  <br/>
  购票列表：{{tickets}}
</template>

<script>
import {defineComponent, ref, onMounted, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";
export default defineComponent({
  name: "order-view",
  setup() {
    const passengers = ref([]);//存储我的乘客的信息
    const passengerOptions = ref([]);
    const passengerChecks = ref([]);
    const dailyTrainTicket = SessionStorage.get(SESSION_ORDER) || {};
    console.log("下单的车次信息", dailyTrainTicket);
    const SEAT_TYPE = window.SEAT_TYPE;
    console.log(SEAT_TYPE)
    // 本车次提供的座位类型seatTypes，含票价，余票等信息，例：
    // {
    //   type: "YDZ",
    //   code: "1",
    //   desc: "一等座",
    //   count: "100",
    //   price: "50",
    // }
    // 关于SEAT_TYPE[KEY]：当知道某个具体的属性xxx时，可以用obj.xxx，当属性名是个变量时，可以使用obj[xxx]
    // 定义空数组seatTypes用于存储车票类型信息
    const seatTypes = [];
    // 遍历SEAT_TYPE对象的每个属性
    for (let KEY in SEAT_TYPE) {
      // 将当前属性名转换为小写形式并赋值给变量key
      let key = KEY.toLowerCase();
      // 判断dailyTrainTicket对象中是否存在与当前车票类型名称相同的属性，并且该属性的值大于等于0
      if (dailyTrainTicket[key] >= 0) {
        // 如果满足条件，将当前车票类型对应的信息组成一个对象并添加到seatTypes数组中
        seatTypes.push({
          type: KEY, // 车票类型名称
          code: SEAT_TYPE[KEY]["code"], // 车票类型代号
          desc: SEAT_TYPE[KEY]["desc"], // 车票类型描述
          count: dailyTrainTicket[key], // 车票数量
          price: dailyTrainTicket[key + 'Price'], // 车票价格，从dailyTrainTicket对象中获取
        })
      }
    }
    // 购票列表，用于界面展示，并传递到后端接口，用来描述：哪个乘客购买什么座位的票
    // {
    //   passengerId: 123,
    //   passengerType: "1",
    //   passengerName: "张三",
    //   passengerIdCard: "12323132132",
    //   seatTypeCode: "1",
    //   seat: "C1"
    // }
    const tickets = ref([]);
    // 勾选或去掉某个乘客时，在购票列表中加上或去掉一张表
    watch(() => passengerChecks.value, (newVal, oldVal)=>{
      console.log("勾选乘客发生变化", newVal, oldVal)
      // 每次有变化时，把购票列表清空，重新构造列表
      tickets.value = [];
      passengerChecks.value.forEach((item) => tickets.value.push({
        passengerId: item.id,
        passengerType: item.type,
        seatTypeCode: seatTypes[0].code,
        passengerName: item.name,
        passengerIdCard: item.idCard
      }))
    }, {immediate: true});
    // 定义处理查询乘客信息的函数
    const handleQueryPassenger = () => {
      // 发送GET请求获取乘客信息
      axios.get("/member/passenger/query-mine").then((response) => {
        // 获取响应数据
        let data = response.data;
        if (data.success) {
          // 如果请求成功，将响应结果中的内容赋值给passengers.value
          passengers.value = data.content;
          passengers.value.forEach((item)=> passengerOptions.value.push({
            label:item.name,
            value:item
          }))
        } else {
          // 如果请求失败，显示错误通知，错误信息为data.message
          notification.error({description: data.message });
        }
      });
    };

    // 在组件挂载后执行
    onMounted(() => {
      // 调用handleQueryPassenger函数，发送查询乘客信息的请求
      handleQueryPassenger();
    });
    console.log("本车次提供的座位：", seatTypes)
    return {
      tickets,
      passengerOptions,
      passengerChecks,
      passengers,
      dailyTrainTicket,
      seatTypes
    };
  },
});
</script>
<style>
.order-train .order-train-main {
  font-size: 18px;
  font-weight: bold;
}
.order-train .order-train-ticket .order-train-ticket-main {
  color: red;
  font-size: 18px;
}
</style>