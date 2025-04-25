const { contextBridge, ipcRenderer } = require('electron/renderer')

contextBridge.exposeInMainWorld('electronAPI', {
    closeWindow: () => ipcRenderer.send('closeWindow'),
    maximizeWindow: () => ipcRenderer.send('maximizeWindow'),
    minimizedWindow: () => ipcRenderer.send('minimizedWindow'),
    getWindowSize: () => ipcRenderer.invoke('getWindowSize')
})