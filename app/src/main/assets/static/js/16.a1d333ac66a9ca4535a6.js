webpackJsonp([16],{"57T+":function(a,s){},AClq:function(a,s,t){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var e={name:"CustomerList",props:{listData:{type:Array,default:function(){return[]}}},components:{MvTag:function(){return t.e(17).then(t.bind(null,"y9Rb"))}},methods:{handleShowRecommend:function(a){this.$emit("recommend",a)}}},n={render:function(){var a=this,s=a.$createElement,t=a._self._c||s;return t("div",a._l(a.listData,function(s){return t("div",{key:s.uuid,staticClass:"customer",class:{recommend:s.recommend}},[t("div",{staticClass:"main-content"},[t("img",{staticClass:"avatar",attrs:{src:s.snapshotImgPath}}),a._v(" "),t("div",{staticClass:"flex-vertical flex-con detail"},[s.recommend&&s.recommendation?t("div",[t("div",{staticClass:"feature"},[a._l(s.recommendation.tags,function(s,e){return t("mv-tag",{key:e,attrs:{size:"large",color:"#f5f5f5"}},[a._v(a._s(s))])}),a._v(" "),s.chronic?t("span",{staticClass:"chronic"},[a._v("慢性病标识")]):a._e()],2),a._v(" "),t("div",{staticClass:"info"},[t("span",{staticClass:"label"},[a._v("年消费频次：")]),t("span",[a._v(a._s(s.recommendation.oneYearCount))])]),a._v(" "),t("div",{staticClass:"info"},[t("span",{staticClass:"label"},[a._v("离药店距离：")]),t("span",[a._v(a._s(s.recommendation.disTanceRangeFlag))])]),a._v(" "),t("div",{staticClass:"info"},[t("span",{staticClass:"label"},[a._v("上次消费时间：")]),t("span",[a._v(a._s(s.recommendation.noComeTime))])]),a._v(" "),t("div",{staticClass:"info"},[t("span",{staticClass:"label"},[a._v("药店消费占比：")]),t("span",[a._v(a._s(s.recommendation.costPersent))])])]):t("div",[s.recommend?a._e():t("span",{staticClass:"label feature-label"},[a._v("\n            基本标签\n          ")]),a._v(" "),t("div",{staticClass:"feature"},[t("mv-tag",{attrs:{size:"large",color:"#f5f5f5"}},[a._v(a._s(1===s.gender?"男":"女")+"性")]),a._v(" "),t("mv-tag",{attrs:{size:"large",color:"#f5f5f5"}},[a._v(a._s(s.age)+"岁")]),a._v(" "),s.chronic?t("span",{staticClass:"chronic"},[a._v("慢性病标识")]):a._e()],1),a._v(" "),t("div",{staticClass:"info"},[t("span",{staticClass:"label"},[a._v("识别位置：")]),t("span",[a._v(a._s(s.cameraName))])])])])]),a._v(" "),t("div",{staticClass:"footer"},[t("div",{staticClass:"potential"},[t("span",{staticClass:"label"},[a._v("消费潜力")]),a._v(" "),t("span",{staticClass:"value"},[a._v(a._s(s.recommend&&s.recommendation?""+s.recommendation.potential:"未匹配"))])]),a._v(" "),s.recommend&&s.recommendation?t("span",{staticClass:"recommend-btn",on:{click:function(t){a.handleShowRecommend(s.recommendation.buyingHabitInfo)}}},[a._v("药品推荐")]):t("span",{staticClass:"recommend-btn"},[a._v("药品推荐")])])])}),0)},staticRenderFns:[]};var i=t("C7Lr")(e,n,!1,function(a){t("57T+")},"data-v-30f272ba",null);s.default=i.exports}});