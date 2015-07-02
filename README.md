# 使用步骤
1. 导入工程,目前整个工程全采用中文注释,为了防止乱码滋生,请修改文本编码方式为 UTF-8。
2. 修改debug.keystore,MD5工具是根据keystore来生成签名的,不同的keystore 生成的签名是不一样的。此 Demo 的签名是用官网提供的 keystore 生成的,若要顺利运行 Demo 程序,需要进行设置或是替换 keystore。除了编译运行官方 DEMO 外,不要直接使用它,出于安全的考虑,用户应该为自己的应用提供一份keystore。
3. keystore在Windows平台替换方式如下: 在 Eclipse 中点击“Windows-Preferences-Android-Build”,在 Custom debug keystore 中选择Demo文件夹中的 debug.keystore,如下图,点击 Apply-OK,运行 Demo ，点击清除缓存即可正常运行。
4. keystore在mac平台替换方式如下:在 Eclipse 中点击“ADT-偏好设置-Android-Build”,在 Custom debug keystore 中选择Demo文件夹中的debug.keystore,如下图,点击 Apply-OK,运行 Demo ，点击清除缓存即可正常运行。
5. 若出现提示：缺少参数app_token，请检查是否已经替换debug.keystore，然后运行demo中的清除缓存。