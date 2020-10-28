## 1、概述

> 说明：这个项目只是个demo用来说明zion第三方api的工作原理，以及接入zion的api应该注意哪些实现细节

要想zion支持配置已有的api，需要考虑以下两个个问题：

1. 用什么方法表示已有的api定义？
2. 怎么绑定api的定义到zion端？

对于第二个问题来说，只要能找到某种方式或者标准定义api, 将其绑定至对应的UI上只需要前端理解api definition，并将api的input和output与对应的组件关联起来似乎是个不错的想法。然而，很难找到一个完整的api定义能表示所有可能的api类型。所以，我们可以设定所有的api的input和output都是json,这样的话就可以通过已有的JsonSchema来理解api的input和output的大致”形状“，而且有些api虽然input不是个json，但是可以通过某种方式用json来描述它。这正是zion接入第三方api的基本思路。  

## 2、Api schema

上面提到用api definition来描述接口，而zion的api definition就是一串json, 我们称之为ApiSchema。实际上为了zion后端方便处理，我们也定义了apiSchema大致的”形状“。[ApiSchema Definition](https://fz-zion.oss-cn-shanghai.aliyuncs.com/json-schema/apiSchemaDefinition.json). 

比如这个demo项目的api Schema如下：

``` json
[
  {
    "apiType": "restful",
    "groupName": "thirdPartyApiDemo",
    "groupApiSchema": {
      "protocol": "http",
      "domain": "localhost",
      "port": 8080,
      "authorization": {
        "tokenType": "Bearer",
        "generator": {
          "endpoint": "http://localhost:8080/generate/token",
          "method": "POST",
          "inputSchema": {
            "type": "object",
            "properties": {
              "userId": { "type": "number" },
              "signature": { "type": "string" }
            },
            "required": ["userId", "signature"],
            "additionalProperties": false
          },
          "outputSchema": {
            "type": "object",
            "properties": {
              "status": { "type": "string", "enum": ["SUCCESS", "FAILED"] }
            },
            "if": {
              "properties": { "status": { "const": "SUCCESS" } }
            },
            "then": {
              "properties": {
                "data": {
                  "type": "object",
                  "properties": {
                    "accessToken": { "type": "string" },
                    "refreshToken": { "type": "string" }
                  },
                  "required": ["accessToken", "refreshToken"]
                }
              },
              "required": ["status", "data"]
            },
            "else": {
              "properties": {
                "message": { "type": "string" }
              },
              "required": ["status", "message"]
            }
          },
          "targetPath": "$.data.accessToken",
          "refreshPath": "$.data.refreshToken"
        },
        "refreshApi": {
          "endpoint": "http://localhost:8080//token/refresh",
          "method": "GET",
          "outputSchema": {
            "type": "object",
            "properties": {
              "status": { "type": "string", "enum": ["SUCCESS", "FAILED"] }
            },
            "if": {
              "properties": { "status": { "const": "SUCCESS" } }
            },
            "then": {
              "properties": {
                "data": {
                  "type": "object",
                  "properties": {
                    "accessToken": { "type": "string" },
                    "refreshToken": { "type": "string" }
                  },
                  "required": ["accessToken", "refreshToken"]
                }
              },
              "required": ["status", "data"]
            },
            "else": {
              "properties": {
                "message": { "type": "string" }
              },
              "required": ["status", "message"]
            }
          },
          "tokenPath": "$.data.accessToken"
        }
      },
      "apis": [
        {
          "name": "getAllUser",
          "path": "/get/all/user",
          "method": "GET",
          "authenticationRequired": false,
          "parameters": {},
          "outputSchema": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "id": { "type": "number" },
                "username": { "type": "string" },
                "password": { "type": "string" },
                "email": { "type": "string", "format": "email" },
                "phoneNumber": { "type": "string" },
                "createdAt": { "type": "string", "format": "date-time" }
              },
              "additionalProperties": false
            }
          }
        },
        {
          "name": "getUser",
          "path": "/get/user",
          "method": "GET",
          "authenticationRequired": false,
          "parameters": {
            "type": "object",
            "properties": {
              "id": { "type": "integer"}
            },
            "required": ["id"]
          },
          "outputSchema": {
            "type": "object",
            "properties": {
              "status": { "type": "string", "enum": ["SUCCESS", "FAILED"] },
              "if": {
                "properties": { "status": { "const": "SUCCESS" } }
              },
              "then": {
                "properties": {
                  "data": {
                    "type": "object",
                    "properties": {
                      "id": { "type": "number" },
                      "username": { "type": "string" },
                      "password": { "type": "string" },
                      "email": { "type": "string", "format": "email" },
                      "phoneNumber": { "type": "string" },
                      "createdAt": { "type": "string", "format": "date-time" }
                    },
                    "additionalProperties": false
                  }
                },
                "required": ["status", "data"]
              },
              "else": {
                "properties": {
                  "message": { "type": "string" }
                },
                "required": ["status", "message"]
              }
            }
          }
        },
        {
          "name": "updateUserName",
          "path": "/update/user/name",
          "method": "POST",
          "authenticationRequired": true,
          "inputSchema": {
            "type": "object",
            "properties": {
              "id": { "type": "number" },
              "username": { "type": "string" }
            },
            "required": ["id", "username"]
          },
          "outputSchema": {
            "type": "object",
            "properties": {
              "status": { "type": "string", "enum": ["SUCCESS", "FAILED"] },
              "if": {
                "properties": { "status": { "const": "SUCCESS" } }
              },
              "then": {
                "properties": {
                  "data": {
                    "type": "object",
                    "properties": {
                      "id": { "type": "number" },
                      "username": { "type": "string" },
                      "password": { "type": "string" },
                      "email": { "type": "string", "format": "email" },
                      "phoneNumber": { "type": "string" },
                      "createdAt": { "type": "string", "format": "date-time" }
                    },
                    "additionalProperties": false
                  }
                },
                "required": ["status", "data"]
              },
              "else": {
                "properties": {
                  "message": { "type": "string" }
                },
                "required": ["status", "message"]
              }
            }
          }
        }
      ]
    }
  }
]

```

如上的Json，便可以表示这个demo的api

> 注意：demo项目定义了多个tokenGenerator，但是上面Json只选择了一种，实际上一般后端颁发Token的接口只有一个，
>
> apis字段应该包含使用同一tokenGenerator的所有api

## 3、流程图

zion接入thirdPartyApi的大致流程

``` sequence
participant thirdPartyDemoApi
participant zion
participant zionbackend
participant appBackend
participant app

note over zion: 用户导入apiSchema,\n编辑时绑定api和组件

zion -> zionbackend: 部署zionApp

note over zionbackend: 生成api config

zionbackend -> appBackend: 生成对应app的backend

zionbackend -> app: 发布app

note over appBackend: 启动时，生成对应apiSchema\n的adapter api

app --> appBackend: 请求thirdPartyDemoApi
appBackend --> thirdPartyDemoApi: appBackend 请求thirdPartyDemoApi
```



zion生成app以及app backend之后的请求流程图：

```` sequence
participant appUser
participant app
participant appBackend
participant thirdPartyApi

appUser --> thirdPartyApi: request token signature
thirdPartyApi --> appUser: response token signature

appUser --> app: input token signature
app -> appBackend: 1、 generate thirdPartyApi token

appBackend --> thirdPartyApi: request user token
thirdPartyApi --> appBackend:

note over appBackend: store accessToken and refreshToken
appBackend -> app: 1、 response success

app -> appBackend: 2、request thirdParty business api
note over appBackend: if token is expired, refresh \naccess token with refreshToken
appBackend --> thirdPartyApi: request with \ntoken store in step 1

note over appBackend: delegate to thirdPartyApi
thirdPartyApi --> appBackend: 

appBackend -> app: 2、response success
````





## 4、鉴权说明

**SecurityController** 中包含两种鉴权方式：

1、直接通过username和password登录获取token。这种方式简单，但是不安全，会导致用户密码的泄露，推荐使用第二种

2、第三方用户先登录，生成tokenSignature，之后在生成的app用户填入tokenSignature方便appBackend获取appUser在第三方的token.

需要注意的是demo中的tokenSignature只是一个泛指，实际上它只要保证如下两个性质就能保证用户信息的安全

	> + 每个用户唯一，且能通过它映射到唯一的用户
	> + 每个tokenSignature使用一次之后就失效

可以看出这个demo中并没有保证tokenSignature使用后失效