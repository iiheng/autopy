from cn.wangyiheng.autopy.services.auto import Auto

def click():
    kotlin_instance = Auto()
    result = kotlin_instance.ensureAutoEnabled()
    return result