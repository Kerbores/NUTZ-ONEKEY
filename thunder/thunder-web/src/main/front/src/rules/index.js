var validateMobile = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入手机号'));
    } else if (!/^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\d{8}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'));
    } else {
        callback();
    }
};
var validateEmail = (rule, value, callback) => {
    if (/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/.test(value)) {
        callback();
    } else {
        callback(new Error("请输入正确的邮箱地址"));
    }
};
export default {
    mobile: [{
        validator: validateMobile,
        trigger: "blur"
    }],
    name: [{required: true, message: "请输入用户名", trigger: "blur"}],
    realName: [{required: true, message: "请输入真实姓名", trigger: "blur"}],
    password: [
        {required: true, message: "请输入密码", trigger: "blur"},
        {min: 8, max: 16, message: "请输入8到16位密码", trigger: "blur"}
    ],
    rePassword: [
        {required: true, message: "请输入密码", trigger: "blur"},
        {min: 8, max: 16, message: "请输入8到16位密码", trigger: "blur"},
    ],
    phone: [
        {required: true, message: "请输入手机号", trigger: "blur"},
        {validator: validateMobile, trigger: "blur"}
    ],
    email: [
        {required: true, message: "请输入电子邮箱", trigger: "blur"},
        {validator: validateEmail, trigger: "blur"}
    ]
}
  