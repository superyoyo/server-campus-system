package com.campus.system;

import com.campus.system.service.Service;

import java.util.HashMap;

public class ServiceContext {
    private HashMap<String, Service> mServiceCache;
    private HashMap<String, Class<? extends Service>> mServiceMenu;
    private Object mLock = new Object();
    private static class InstanceHolder{
        static ServiceContext sInstance = new ServiceContext();
    }

    public static ServiceContext getInstance(){
        return InstanceHolder.sInstance;
    }

    private ServiceContext(){
        mServiceMenu = new HashMap();
        mServiceCache = new HashMap();
    }

    public synchronized void registeService(String service, Class<? extends Service> serviceClass){
        mServiceMenu.put(service, serviceClass);
    }

    public Service getSystemService(String service){
        if(mServiceCache.containsKey(service)){
            return mServiceCache.get(service);
        }
        synchronized (mLock){
            if(mServiceCache.containsKey(service)){
                return mServiceCache.get(service);
            }
            Class<? extends Service> serviceClass = mServiceMenu.get(service);
            if(serviceClass == null){
                throw new RuntimeException("没有找到对应的服务，请先注册此服务");
            }
            try {
                Service serviceInstance = serviceClass.newInstance();
                serviceInstance.init(this);
                mServiceCache.put(service, serviceInstance);
                return serviceInstance;
            }catch (IllegalAccessException e){
                //TODO 加载Service错误
            }catch (InstantiationException e){
                //TOOD 无法实例化Service
            }

            return null;
        }
    }
}
