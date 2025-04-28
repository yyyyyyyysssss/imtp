const { app, Tray, BrowserWindow, Menu, nativeImage, ipcMain } = require('electron')
const path = require('node:path')

const createWindow = (width, height, minWidth = 0, minHeight = 0, maximizable = true) => {
    const win = new BrowserWindow({
        frame: false, //是否显示窗口控制按钮
        width: width,
        minWidth: minWidth,
        height: height,
        minHeight: minHeight,
        icon: path.join(__dirname, './public/icon.png'),
        show: false, //准备完成之前不显示
        maximizable: maximizable, //是否可以最大化
        webPreferences: {
            contextIsolation: true, //设置渲染进程中的代码无法直接访问主进程的对象和变量
            nodeIntegration: false, //设置渲染进程无法直接访问 Node.js 的 API 只能通过ipc通信访问
            preload: path.join(__dirname, 'preload.js')
        }
    })
    //窗户准备完成
    win.once('ready-to-show', () => {
        win.show();  // 显示窗口
    });

    //当窗口获取焦点时 关闭闪烁
    win.once('focus', () => {
        win.flashFrame(false)
    })

    //窗口大小变化事件
    win.on('resize', () => {
        const [width, height] = win.getSize()
        win.webContents.send('resize', {
            width: width,
            height: height,
            isMaximized: win.isMaximized()
        })
    })

    // win.loadFile('index.html')
    win.loadURL('http://localhost:3000')

    //开发者工具
    // win.webContents.openDevTools()

    win.on('closed', () => {

    })

    return win
}

let currentWindow;
let tray;

app.whenReady().then(() => {
    currentWindow = createWindow(350, 600, 0, 0, false)
    //获取当前窗口大小
    ipcMain.handle('getWindowSize', () => {
        return currentWindow.getSize()
    })
    //登录成功
    ipcMain.handle('loginSuccess', () => {
        currentWindow.close()
        //设置系统托盘
        const icon = nativeImage.createFromPath('./public/icon.png')
        tray = new Tray(icon)
        tray.setToolTip('氧气')
        tray.setContextMenu(Menu.buildFromTemplate([
            {
                label: '退出',
                click: () => {
                    app.quit()
                }
            }
        ]))
        tray.on('click', () => {
            currentWindow.show()
        })
        //打开新窗口
        currentWindow = createWindow(1000, 750, 700, 500, true)
    })
    //关闭窗口
    ipcMain.on('closeWindow', (event) => {
        currentWindow.close()
    })
    //隐藏窗口
    ipcMain.on('hideWindow', (event) => {
        currentWindow.hide()
    })
    //退出
    ipcMain.on('quit', (event) => {
        app.quit()
    })
    //窗口最大化
    ipcMain.on('maximizeWindow', (event) => {
        if (currentWindow.isMaximized()) {
            currentWindow.restore(); // 如果窗口已经最大化，则还原
        } else {
            currentWindow.maximize(); // 否则最大化窗口
        }
    })
    //窗口最小化
    ipcMain.on('minimizedWindow', (event) => {
        currentWindow.minimize()
    })
    //收到消息
    ipcMain.on('receiveMessage', (event, message) => {
        //当窗口最小化时 则闪烁
        if (currentWindow.isMinimized()) {
            currentWindow.flashFrame(true)
        }
    })

    app.on('activate', () => {
        // 在 macOS 系统内, 如果没有已开启的应用窗口
        // 点击托盘图标时通常会重新创建一个新窗口
        if (BrowserWindow.getAllWindows().length === 0) {
            currentWindow = createWindow(1000, 750, 700, 500)
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