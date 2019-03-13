$(function() {
  var param = {
    phoneNumber: null,
    verifyCode: null,
    time: 10, //获取验证码间隔
    eyesEle: $('.mv-eyes'), //眼睛图标节点
    clearInputEle: $('.need-clear'), //需要清除的input节点
    clearEle: $('.mv-clear') //清除图标节点
  },FN = {
    init: function() {
      module.localData.removeData('mtk')
      //缓存获取手机号
      if(module.localData.getData('userInfo')) {
        var phone = JSON.parse(module.localData.getData('userInfo')).phone
        $('#telephone').val(phone)
      }
      $('#login').on('tap',function() {//登录
        param.phoneNumber = $('#telephone').val()
        param.verifyCode = $('#pwd').val()
        if(FN.checkMobile(param.phoneNumber) && FN.checkPw(param.verifyCode) && FN.checkAgree()) {
          module.loading.show()
          module.$http(API.base.login,{
            account: param.phoneNumber,
            pwd: md5(param.verifyCode),
            reqRole: 1,
            isApp: true
          },function () {
            console.log('登录状态==>',this)
            //缓存mtk和userId
            module.localData.setData('mtk',this.resData.mtk)
            module.localData.setData('userId',this.resData.userId)
            console.log('mtk==>',module.localData.getData('mtk'))
            //FN.getUserInfo(this.resData.roles[0]) //获取用户信息
            FN.getUserInfo(this.resData.userId) //获取用户信息
          });
        }
      })
    },
    checkMobile: function (telNum) { //校验手机号
      if (/^1[3|4|5|7|8|9][0-9]\d{8}$/.test(telNum)) {
        return true;
      }
      module.alert('请输入正确的手机号！');
      return false;
    },
    checkPw: function (pw) { //校验密码
      if (/^[a-zA-Z\d_]{6,}$/.test(pw)) {
        return true;
      }
      module.alert('密码格式不正确！');
      return false;
    },
    checkAgree: function () { //校验用户协议
      if ($('.login-protocol.active').length) {
        return true;
      }
      module.alert('请先同意用户协议！');
      return false;
    },
    //获取用户信息
    getUserInfo: function (userId) { 
      module.$http(API.base.getUserInfo,{},function () {
        console.log('用户信息==>',this);
        var userInfo = {
          userId : userId,//* userid */
          roles : this.resData.roleName,//* 角色名称 */
          roleId : this.resData.roleId,//* 角色名称 */
          realName : this.resData.realName,//* 会员真实名 */
          profilePic : this.resData.profilePic,//* 会员头像 */
          accountName : this.resData.userName,//* 账户名 */
          term : this.resData.term,//* 有效期限 */
          phone: this.resData.phone //* 手机号 */
        }
        
        module.localData.setData('userInfo',JSON.stringify(userInfo)) //* 缓存用户信息 */
        FN.getBrands(userInfo.userId) //* 获取店铺列表 */ 
        //===================================
        // if(roleName == 'brandManager') {
        //   userInfo.roles = 'brandManager'
        //   module.localData.setData('userInfo',JSON.stringify(userInfo)) //* 缓存用户信息 */
        //   FN.getBrands(this.resData.userId) //* 获取店铺列表 */ 
        // }else {
        //   FN.getRoleName(userInfo,this.resData.userId)
        // }
        //=========================
        //FN.getRoleName(userInfo,this.resData.userId) //获取角色信息
      });
    },
    //获取用户角色信息
    getRoleName: function(userInfo,userId) { 
      module.$http(API.base.getUserRole,{},function () {
        console.log('角色信息==>',this)
        if(this.resData && this.resData.length > 0) {
          userInfo.roles = this.resData[0].exRoleName //roleName
          userInfo.roleId = this.resData[0].exRoleId //roelId
        }else {
          userInfo.roles = ''
          userInfo.roleId = ''
          module.alert('用户角色信息不存在')
        }
        module.localData.setData('userInfo',JSON.stringify(userInfo)) //* 缓存用户信息 */
        FN.getBrands(userId) //* 获取店铺列表 */ 
      });
    },
    //获取店铺列表
    getShops: function(id,brandId) { 
      console.log('brandId==>',brandId)
      module.$http(API.base.getShops,{
        brandId: brandId, 
        userId: id  //userId
      },function () {
        module.loading.hide() //隐藏loading
        console.log('shopInfo==>',this)
        console.log('this.resData==>',this.resData)
        if(this.resData && this.resData.length > 0) {
          if(module.localData.getData('shopInfo')) { //存在shopInfo缓存
            var shopInfo = JSON.parse(module.localData.getData('shopInfo')) 
            if(!FN.checkData(shopInfo.shopId,this.resData,'shopId')) { //判断shopInfo缓存是否符合当前账户
              module.localData.setData('shopInfo',JSON.stringify(this.resData[0]))
            }
          }else {
            module.localData.setData('shopInfo',JSON.stringify(this.resData[0]))
          }
          
          module.localData.setData('shopList',JSON.stringify(this.resData))
          /* 登录成功 */
          module.alert('登录成功')
          /*通知登录成功*/
          module.postToNativeMessage({
            call: 'loginSuccess'
          })  
          /*关闭登录界面*/
          module.closeToWindow()
        }else {
          module.alert('店铺信息为空')
          module.loading.hide()
        }
      });
    },
    //获取品牌列表
    getBrands: function(id) {
      module.$http(API.base.getAllBrand,{
        userId: id //userId
      },function () {
        console.log('brandInfo==>',this) 
        if(this.resData.rows && this.resData.rows.length > 0) {
          if(module.localData.getData('brandInfo')) { //存在brandInfo缓存
            var brandInfo = module.localData.getData('brandInfo')
            if(!FN.checkData(brandInfo,this.resData.rows,'brandId')) { //判断brandInfo缓存是否符合当前账户
              module.localData.setData('brandInfo',this.resData.rows[0].brandId) //* 第一条brandId */
              FN.getShops(id,this.resData.rows[0].brandId) 
            }else {
              FN.getShops(id,module.localData.getData('brandInfo'))
            }
          }else {
            module.localData.setData('brandInfo',this.resData.rows[0].brandId)
            FN.getShops(id,this.resData.rows[0].brandId)
          }
      
          module.localData.setData('brandList',JSON.stringify(this.resData.rows)) //* brandList数组 */
        }else {
          module.alert('角色品牌信息不存在')
          module.loading.hide()
        }
      });
    },
    //显示隐藏密码
    toogleEyes: function(obj) {
      if(obj.attr('data-state') == 'on') {
        obj.attr({
          'src':'images/eye2.png',
          'data-state':'off'
        }).prev().attr('type','text')
      }else {
        obj.attr({
          'src':'images/eye1.png',
          'data-state':'on'
        }).prev().attr('type','password')
      }
    },
    //判断是否在缓存内
    checkData: function(param,data,checkPa) {
      console.log('校验param==>',param)
      var flag = 1,Len = data.length
      for(var i = 0;i < Len;i++) {
        if(flag) {
          if(param == data[i][checkPa]) {
            console.log('缓存存在数据==>',param)
            flag = 0
            return true
          }
        }
      }
    }
  }

  /**初始化 */
  FN.init() 

  /**点击切换 */
  param.eyesEle.tap(function() {
    FN.toogleEyes($(this))
  })

  /**点击切换 */
  $('.login-protocol').on('tap',function() {
    $(this).toggleClass('active')
  })

  /**监听输入 */
  param.clearInputEle.on('input',function() {
    ($(this).val() > 0) ? $('.mv-clear').addClass('clear-on') : $('.mv-clear').removeClass('clear-on')
  })

  /**清空输入 */
  param.clearEle.tap(function() {
    $(this).removeClass('clear-on').prev().val('')
  })

  //logo 点击跳转 host 设置
  var time = 1,clearTime = null;
  $('.login-container-logo').tap(function() {
    if(time == 10) {
      module.urlToLocation({
        title: '参数配置',
        url: 'pages/userAgreement/setHost.html'
      });
    } else {
      time ++;
      clearTimeout(clearTime);
      clearTime = setTimeout(function() {
        time = 1;
      },2000);
    }
  });
})