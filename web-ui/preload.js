const { contextBridge, ipcRenderer } = require('electron/renderer')

contextBridge.exposeInMainWorld('electronAPI', {
    closeWindow: () => ipcRenderer.send('closeWindow'),
    maximizeWindow: () => ipcRenderer.send('maximizeWindow'),
    minimizedWindow: () => ipcRenderer.send('minimizedWindow'),
    getWindowSize: () => ipcRenderer.invoke('getWindowSize'),
    onResize: (callback) => ipcRenderer.on('resize', (_event, value) => callback(value))
})