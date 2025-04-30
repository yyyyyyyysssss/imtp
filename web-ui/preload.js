const { contextBridge, ipcRenderer } = require('electron/renderer')

contextBridge.exposeInMainWorld('electronAPI', {
    closeWindow: () => ipcRenderer.send('closeWindow'),
    hideWindow: () => ipcRenderer.send('hideWindow'),
    quit: () => ipcRenderer.send('quit'),
    maximizeWindow: () => ipcRenderer.send('maximizeWindow'),
    minimizedWindow: () => ipcRenderer.send('minimizedWindow'),
    getWindowSize: () => ipcRenderer.invoke('getWindowSize'),
    onResize: (callback) => ipcRenderer.on('resize', (_event, value) => callback(value)),
    loginSuccess: () => ipcRenderer.invoke('loginSuccess'),
    logout: () => ipcRenderer.invoke('logout'),
    checkLogin: (callback) => ipcRenderer.on('checkLogin', (_event) => callback()),
    receiveMessage: (message) => ipcRenderer.send('receiveMessage', message),
    onQuit: (callback) => ipcRenderer.on('quit', (_event) => callback()),
})