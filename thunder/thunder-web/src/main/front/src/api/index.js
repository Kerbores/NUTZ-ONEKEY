import User from "./acl/user";
import Role from "./acl/role";
import Permission from "./acl/permission";

import CodeBook from "./codebook/code";
import CodeBookGroup from "./codebook/group";

import Setting from "./setting";

import APM from "./monitor/apm";
import Trace from "./monitor/trace";

import Branch from "./department/branch";
import Department from "./department/department";

export default {
    User: User,
    Role: Role,
    Permission: Permission,
    CodeBook: CodeBook,
    CodeBookGroup: CodeBookGroup,
    Setting: Setting,
    APM: APM,
    Trace: Trace,
    Branch: Branch,
    Department: Department
};
