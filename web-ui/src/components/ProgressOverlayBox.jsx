import {
    CircularProgressbar,
    buildStyles
} from "react-circular-progressbar";
import 'react-circular-progressbar/dist/styles.css'


const ProgressOverlayBox = ({ enabled, progress = 5, children }) => {


    return (
        <div
            style={{
                position: 'relative',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center'
            }}
        >
            {children}
            {enabled && (
                // <div
                //     style={{
                //         position: 'absolute',
                //         display: 'flex',
                //         width: '100%',
                //         height: '100%',
                //         justifyContent: 'center',
                //         alignItems: 'center',
                //         backgroundColor: 'rgba(0, 0, 0, 0.3)',
                //     }}
                // >
                    <div
                        style={{
                            position: 'absolute',
                            width: 35,
                            height: 35
                        }}
                    >
                        <CircularProgressbar
                            value={progress}
                            strokeWidth={50}
                            styles={buildStyles({
                                strokeLinecap: "butt",
                                trailColor: 'rgba(0, 0, 0, 0.3)',
                                pathColor: "white"
                            })}
                        />
                    </div>
                // </div>
            )}
        </div>
    )

}

export default ProgressOverlayBox