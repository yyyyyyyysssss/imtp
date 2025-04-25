const { app, BrowserWindow, Menu, ipcMain } = require('electron')
const path = require('node:path')

const createWindow = () => {
    const win = new BrowserWindow({
        frame: false,
        width: 1000,
        height: 750,
        webPreferences: {
            contextIsolation: true,
            nodeIntegration: false,
            preload: path.join(__dirname, 'preload.js')
        }
    })

    ipcMain.on('closeWindow', (event) => {
        win.close()
    })

    ipcMain.on('maximizeWindow', (event) => {
        if (win.isMaximized()) {
            win.restore(); // 如果窗口已经最大化，则还原
        } else {
            win.maximize(); // 否则最大化窗口
        }
    })

    ipcMain.on('minimizedWindow', (event) => {
        win.minimize()
    })

    // win.loadFile('index.html')
    win.loadURL('http://localhost:3000')

    //开发者工具
    // win.webContents.openDevTools()

    win.on('closed', () => {
        console.log('closed')
    })

    return win
}


const getWindowSize = (win) => {
    return win.getSize()
}

app.whenReady().then(() => {
    let mainWindow = createWindow()
    ipcMain.handle('getWindowSize',() => getWindowSize(mainWindow))
    app.on('activate', () => {
        // 在 macOS 系统内, 如果没有已开启的应用窗口
        // 点击托盘图标时通常会重新创建一个新窗口
        if (BrowserWindow.getAllWindows().length === 0) {
            mainWindow = createWindow()
        }
    })
})

// 除了 macOS 外，当所有窗口都被关闭的时候退出程序。 因此, 通常
// 对应用程序和它们的菜单栏来说应该时刻保持激活状态, 
// 直到用户使用 Cmd + Q 明确退出
app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit()
    }
})

//隐藏默认菜单
Menu.setApplicationMenu(null)