import React from 'react';
import { createIcon } from 'native-base';
import { Path, G } from 'react-native-svg';

export const GoogleIcon = createIcon({
    viewBox: '0 0 48 48',
    path: <G>
        <Path fill='#fbc02d' d='M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12 s5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24s8.955,20,20,20 s20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z' />
        <Path fill='#e53935' d='M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039 l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z' />
        <Path fill='#4caf50' d='M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36 c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z' />
        <Path fill='#1565c0' d='M43.611,20.083L43.595,20L42,20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.571 c0.001-0.001,0.002-0.001,0.003-0.002l6.19,5.238C36.971,39.205,44,34,44,24C44,22.659,43.862,21.35,43.611,20.083z' />
    </G>
})

export const GithubIcon = createIcon({
    viewBox: '0 0 64 64',
    d: 'M32 6C17.641 6 6 17.641 6 32c0 12.277 8.512 22.56 19.955 25.286-.592-.141-1.179-.299-1.755-.479V50.85c0 0-.975.325-2.275.325-3.637 0-5.148-3.245-5.525-4.875-.229-.993-.827-1.934-1.469-2.509-.767-.684-1.126-.686-1.131-.92-.01-.491.658-.471.975-.471 1.625 0 2.857 1.729 3.429 2.623 1.417 2.207 2.938 2.577 3.721 2.577.975 0 1.817-.146 2.397-.426.268-1.888 1.108-3.57 2.478-4.774-6.097-1.219-10.4-4.716-10.4-10.4 0-2.928 1.175-5.619 3.133-7.792C19.333 23.641 19 22.494 19 20.625c0-1.235.086-2.751.65-4.225 0 0 3.708.026 7.205 3.338C28.469 19.268 30.196 19 32 19s3.531.268 5.145.738c3.497-3.312 7.205-3.338 7.205-3.338.567 1.474.65 2.99.65 4.225 0 2.015-.268 3.19-.432 3.697C46.466 26.475 47.6 29.124 47.6 32c0 5.684-4.303 9.181-10.4 10.4 1.628 1.43 2.6 3.513 2.6 5.85v8.557c-.576.181-1.162.338-1.755.479C49.488 54.56 58 44.277 58 32 58 17.641 46.359 6 32 6zM33.813 57.93C33.214 57.972 32.61 58 32 58 32.61 58 33.213 57.971 33.813 57.93zM37.786 57.346c-1.164.265-2.357.451-3.575.554C35.429 57.797 36.622 57.61 37.786 57.346zM32 58c-.61 0-1.214-.028-1.813-.07C30.787 57.971 31.39 58 32 58zM29.788 57.9c-1.217-.103-2.411-.289-3.574-.554C27.378 57.61 28.571 57.797 29.788 57.9z'
})

export const MicrosoftIcon = createIcon({
    viewBox: '0 0 48 48',
    path: <G>
        <Path fill='#ff5722' transform='rotate(-180 14 14)' d='M6 6H22V22H6z' />
        <Path fill='#4caf50' transform='rotate(-180 34 14)' d='M26 6H42V22H26z' />
        <Path fill='#ffc107' transform='rotate(-180 34 34)' d='M26 26H42V42H26z' />
        <Path fill='#03a9f4' transform='rotate(-180 14 34)' d='M6 26H22V42H6z' />
    </G>
})

export const ChatTabIcon = createIcon({
    viewBox: '0 0 50 50',
    d: 'M 25 4.0625 C 12.414063 4.0625 2.0625 12.925781 2.0625 24 C 2.0625 30.425781 5.625 36.09375 11 39.71875 C 10.992188 39.933594 11 40.265625 10.71875 41.3125 C 10.371094 42.605469 9.683594 44.4375 8.25 46.46875 L 7.21875 47.90625 L 9 47.9375 C 15.175781 47.964844 18.753906 43.90625 19.3125 43.25 C 21.136719 43.65625 23.035156 43.9375 25 43.9375 C 37.582031 43.9375 47.9375 35.074219 47.9375 24 C 47.9375 12.925781 37.582031 4.0625 25 4.0625 Z M 25 5.9375 C 36.714844 5.9375 46.0625 14.089844 46.0625 24 C 46.0625 33.910156 36.714844 42.0625 25 42.0625 C 22.996094 42.0625 21.050781 41.820313 19.21875 41.375 L 18.65625 41.25 L 18.28125 41.71875 C 18.28125 41.71875 15.390625 44.976563 10.78125 45.75 C 11.613281 44.257813 12.246094 42.871094 12.53125 41.8125 C 12.929688 40.332031 12.9375 39.3125 12.9375 39.3125 L 12.9375 38.8125 L 12.5 38.53125 C 7.273438 35.21875 3.9375 29.941406 3.9375 24 C 3.9375 14.089844 13.28125 5.9375 25 5.9375 Z'
})

export const ChatTabIconSelected = createIcon({
    viewBox: '0 0 256 256',
    path: <G>
        <Path fill='#70bfff' transform='scale(5.12,5.12)' d='M25,4c-12.68359,0 -23,8.97266 -23,20c0,6.1875 3.33594,12.06641 8.94922,15.83984c-0.13281,1.05078 -0.66406,3.60156 -2.76562,6.58594l-1.10547,1.56641l1.97656,0.00781c5.42969,0 9.10156,-3.32812 10.30859,-4.60547c1.83203,0.40234 3.72656,0.60547 5.63672,0.60547c12.68359,0 23,-8.97266 23,-20c0,-11.02734 -10.31641,-20 -23,-20z' />
    </G>
})

export const AddIcon = createIcon({
    viewBox: '0 0 24 24',
    d: 'M 11.5 2 C 6.259214 2 2 6.2592178 2 11.5 C 2 16.740782 6.259214 21 11.5 21 C 16.740786 21 21 16.740782 21 11.5 C 21 6.2592178 16.740786 2 11.5 2 z M 11.5 3 C 16.200346 3 20 6.7996569 20 11.5 C 20 16.200343 16.200346 20 11.5 20 C 6.7996538 20 3 16.200343 3 11.5 C 3 6.7996569 6.7996538 3 11.5 3 z M 11.492188 6.9921875 A 0.50005 0.50005 0 0 0 11 7.5 L 11 11 L 7.5 11 A 0.50005 0.50005 0 1 0 7.5 12 L 11 12 L 11 15.5 A 0.50005 0.50005 0 1 0 12 15.5 L 12 12 L 15.5 12 A 0.50005 0.50005 0 1 0 15.5 11 L 12 11 L 12 7.5 A 0.50005 0.50005 0 0 0 11.492188 6.9921875 z'
})


export const VoiceStaticIcon = createIcon({
    viewBox: '0 0 24 24',
    path: <G>
        <Path fill='#000000'  d='M12.0002 19C11.4479 19 11.0002 19.4477 11.0002 20C11.0002 20.5523 11.4479 21 12.0002 21V19ZM12.0102 21C12.5625 21 13.0102 20.5523 13.0102 20C13.0102 19.4477 12.5625 19 12.0102 19V21ZM14.6907 17.04C15.0993 17.4116 15.7317 17.3817 16.1033 16.9732C16.475 16.5646 16.445 15.9322 16.0365 15.5605L14.6907 17.04ZM18.0541 13.3403C18.4626 13.7119 19.0951 13.682 19.4667 13.2734C19.8384 12.8649 19.8084 12.2324 19.3999 11.8608L18.0541 13.3403ZM7.96394 15.5605C7.55539 15.9322 7.52546 16.5646 7.89708 16.9732C8.26871 17.3817 8.90117 17.4116 9.30971 17.04L7.96394 15.5605ZM4.60055 11.8608C4.192 12.2324 4.16207 12.8649 4.53369 13.2734C4.90532 13.682 5.53778 13.7119 5.94633 13.3403L4.60055 11.8608ZM12.0002 21H12.0102V19H12.0002V21ZM12.0002 16C13.0369 16 13.9795 16.3931 14.6907 17.04L16.0365 15.5605C14.9715 14.5918 13.5538 14 12.0002 14V16ZM12.0002 11C14.3321 11 16.4548 11.8855 18.0541 13.3403L19.3999 11.8608C17.4468 10.0842 14.8489 9 12.0002 9V11ZM9.30971 17.04C10.0209 16.3931 10.9635 16 12.0002 16V14C10.4466 14 9.02893 14.5918 7.96394 15.5605L9.30971 17.04ZM5.94633 13.3403C7.54565 11.8855 9.66836 11 12.0002 11V9C9.15148 9 6.55365 10.0842 4.60055 11.8608L5.94633 13.3403Z' />
    </G>
})


export const AudioOutlined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M842 454c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8 0 140.3-113.7 254-254 254S258 594.3 258 454c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8 0 168.7 126.6 307.9 290 327.6V884H326.7c-13.7 0-24.7 14.3-24.7 32v36c0 4.4 2.8 8 6.2 8h407.6c3.4 0 6.2-3.6 6.2-8v-36c0-17.7-11-32-24.7-32H548V782.1c165.3-18 294-158 294-328.1z'/>
        <Path d='M512 624c93.9 0 170-75.2 170-168V232c0-92.8-76.1-168-170-168s-170 75.2-170 168v224c0 92.8 76.1 168 170 168z m-94-392c0-50.6 41.9-92 94-92s94 41.4 94 92v224c0 50.6-41.9 92-94 92s-94-41.4-94-92V232z'/>
    </G>
})

export const AudioMuteOutlined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M682 455V311l-76 76v68c-0.1 50.7-42 92.1-94 92-19.1 0.1-36.8-5.4-52-15l-54 55c29.1 22.4 65.9 36 106 36 93.8 0 170-75.1 170-168z'/>
        <Path d='M833 446h-60c-4.4 0-8 3.6-8 8 0 140.3-113.7 254-254 254-63 0-120.7-23-165-61l-54 54c48.9 43.2 110.8 72.3 179 81v102H326c-13.9 0-24.9 14.3-25 32v36c0.1 4.4 2.9 8 6 8h408c3.2 0 6-3.6 6-8v-36c0-17.7-11-32-25-32H547V782c165.3-17.9 294-157.9 294-328 0-4.4-3.6-8-8-8zM846.1 68.3l-43.5-41.9c-3.1-3-8.1-3-11.2 0.1l-129 129C634.3 101.2 577 64 511 64c-93.9 0-170 75.3-170 168v224c0 6.7 0.4 13.3 1.2 19.8l-68 68c-10.5-27.9-16.3-58.2-16.2-89.8-0.2-4.4-3.8-8-8-8h-60c-4.4 0-8 3.6-8 8 0 53 12.5 103 34.6 147.4l-137 137c-3.1 3.1-3.1 8.2 0 11.3l42.7 42.7c3.1 3.1 8.2 3.1 11.3 0L846.2 79.8l0.1-0.1c3.1-3.2 3-8.3-0.2-11.4zM417 401V232c0-50.6 41.9-92 94-92 46 0 84.1 32.3 92.3 74.7L417 401z'/>
    </G>
})


export const HangUpOutlined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path fill="#ffffff" d='M934.528 554.325333c-14.506667 54.229333-51.925333 69.674667-113.493333 60.970667-72.362667-10.24-133.589333-29.653333-129.109334-92.16 0 0 5.717333-67.285333-170.325333-72.405333-205.44-5.290667-196.693333 74.410667-196.693333 74.410666-0.085333 73.386667-50.773333 77.44-122.965334 88.192-72.234667 10.752-116.394667-7.168-125.269333-87.381333-17.152-154.624 265.386667-194.944 389.333333-196.48 0 0 189.013333-2.858667 279.594667 23.765333l48.768 13.866667c67.968 21.973333 128.981333 61.952 143.146667 118.826667 0 0 8.362667 25.898667-2.986667 68.394666z'/>
    </G>
})

export const VolumeUpLined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M426.666667 308.053333L281.728 426.666667H128v170.666666h153.728L426.666667 715.946667V308.053333zM251.264 682.666667H85.333333a42.666667 42.666667 0 0 1-42.666666-42.666667V384a42.666667 42.666667 0 0 1 42.666666-42.666667h165.930667l225.877333-184.832a21.333333 21.333333 0 0 1 34.858667 16.512v677.973334a21.333333 21.333333 0 0 1-34.858667 16.512L251.306667 682.666667z m576.725333 176.384l-60.416-60.416A383.061333 383.061333 0 0 0 896 512a383.232 383.232 0 0 0-140.970667-297.301333l60.586667-60.586667A468.309333 468.309333 0 0 1 981.333333 512c0 137.514667-59.136 261.205333-153.344 347.050667z m-151.168-151.168l-60.672-60.672A170.368 170.368 0 0 0 682.666667 512c0-61.013333-32-114.56-80.213334-144.725333l61.397334-61.397334A255.616 255.616 0 0 1 768 512c0 78.592-35.413333 148.906667-91.178667 195.882667z'/>
    </G>
})

export const VolumeMuteUpLined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M426.666667 308.053333L281.728 426.666667H128v170.666666h153.728L426.666667 715.946667V308.053333zM251.264 682.666667H85.333333a42.666667 42.666667 0 0 1-42.666666-42.666667V384a42.666667 42.666667 0 0 1 42.666666-42.666667h165.930667l225.877333-184.832a21.333333 21.333333 0 0 1 34.858667 16.512v677.973334a21.333333 21.333333 0 0 1-34.858667 16.512L251.306667 682.666667z m619.733333-170.666667l150.869334 150.869333-60.330667 60.330667L810.666667 572.330667l-150.869334 150.869333-60.330666-60.330667L750.336 512 599.466667 361.130667l60.330666-60.330667L810.666667 451.669333l150.869333-150.869333 60.330667 60.330667L870.997333 512z'/>
    </G>
})

export const VideoOnLined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path fill="#000000" d='M873.770667 314.922667c19.797333-12.202667 37.632-2.048 37.632 18.304v335.232c0 24.362667-15.872 32.512-37.632 18.261333l-112.938667-67.029333c-19.797333-12.202667-37.632-26.453333-37.632-46.72V424.618667c0-18.261333 17.834667-30.464 37.632-42.666667l112.938667-67.029333zM207.232 288h391.936c43.562667 0 79.232 32 79.232 71.125333v305.749334c0 39.125333-35.669333 71.125333-79.232 71.125333H207.232C163.669333 736 128 704 128 664.874667V359.125333C128 320 163.669333 288 207.232 288z'/>
    </G>
})

export const VideoOffLined = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path fill="#000000" d='M873.770667 314.922667c19.797333-12.202667 37.632-2.048 37.632 18.304v335.232c0 24.362667-15.872 32.512-37.632 18.261333l-112.938667-67.029333c-19.797333-12.202667-37.632-26.453333-37.632-46.72V424.618667c0-18.261333 17.834667-30.464 37.632-42.666667l112.938667-67.029333zM385.152 288h214.016c43.562667 0 79.232 32 79.232 71.125333v222.122667L385.152 288z m256.042667 437.077333a85.333333 85.333333 0 0 1-42.026667 10.922667H207.232C163.669333 736 128 704 128 664.874667V359.125333c0-38.186667 34.005333-69.632 76.16-71.082666l437.034667 437.034666zM145.28 183.893333l45.226667-45.226666 678.826666 678.826666-45.226666 45.226667z'/>
    </G>
})


export const PhoneOutlined = createIcon({
    viewBox: '0 0 1025 1024',
    path: <G>
        <Path fill="#ffffff" d='M671.061333 112.917333a64 64 0 0 1 90.496 0l134.037334 134.016 3.242666 7.573334-39.232 16.789333c39.232-16.789333 39.253333-16.768 39.253334-16.725333l0.042666 0.085333 0.064 0.170667 0.170667 0.384a78.293333 78.293333 0 0 1 1.365333 3.562666c0.746667 2.090667 1.642667 4.8 2.581334 8.149334 1.92 6.698667 4.032 15.893333 5.546666 27.434666 3.072 23.146667 3.690667 55.466667-4.352 95.616-16.128 80.64-66.432 189.717333-195.498666 318.805334-129.088 129.066667-238.165333 179.370667-318.805334 195.498666-40.170667 8.042667-72.469333 7.424-95.616 4.352a193.216 193.216 0 0 1-27.434666-5.546666 128.96 128.96 0 0 1-11.733334-3.946667l-0.362666-0.170667-0.170667-0.064-0.085333-0.042666c-0.042667 0-0.064-0.021333 16.725333-39.253334l-16.789333 39.232-7.552-3.242666-134.037334-134.037334a64 64 0 0 1 0-90.496l120.682667-120.682666a64 64 0 0 1 90.496 0l83.861333 83.84c5.269333-2.944 11.306667-6.485333 17.984-10.666667 27.733333-17.322667 66.624-45.525333 109.354667-88.256 42.730667-42.730667 70.933333-81.621333 88.256-109.354667 4.181333-6.677333 7.722667-12.714667 10.666667-17.984l-83.84-83.84a64 64 0 0 1 0-90.517333l120.682666-120.682667z m15.082667 286.613334c39.616 15.850667 39.616 15.850667 39.594667 15.872v0.021333l-0.021334 0.064-0.064 0.128-0.149333 0.362667-0.426667 1.045333-1.472 3.349333c-1.237333 2.773333-3.029333 6.613333-5.44 11.434667a479.36 479.36 0 0 1-22.250666 39.36c-20.373333 32.597333-52.501333 76.693333-100.266667 124.458667-47.786667 47.786667-91.882667 79.914667-124.48 100.288a479.36 479.36 0 0 1-39.36 22.250666 296.874667 296.874667 0 0 1-14.784 6.912l-1.045333 0.426667-0.362667 0.149333-0.128 0.064h-0.064l-0.021333 0.021334-15.872-39.594667 15.850666 39.616-26.133333 10.453333-110.4-110.421333-90.517333 90.517333 105.664 105.664c2.922667 0.682667 6.762667 1.429333 11.52 2.069334 14.336 1.877333 37.184 2.666667 67.733333-3.434667 60.8-12.16 155.370667-52.352 275.178667-172.181333 119.829333-119.808 160.021333-214.4 172.181333-275.2 6.101333-30.506667 5.333333-53.376 3.413333-67.690667a120.256 120.256 0 0 0-2.048-11.541333l-105.664-105.664-90.517333 90.517333 110.421333 110.4-10.453333 26.133333-39.616-15.850666z'/>
    </G>
})

export const VideoCallMessageIcon = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M179.796 369.171c-10.762 0-19.489 8.725-19.489 19.487 0 10.766 8.727 19.489 19.489 19.489 10.764 0 19.489-8.723 19.489-19.489 0-10.762-8.726-19.487-19.489-19.487z m769.18-57.106a19.409 19.409 0 0 0-19.888 0.743L705.996 460.173V349.68c0-43.053-34.904-77.953-77.955-77.953H140.818c-43.053 0-77.953 34.901-77.953 77.953v389.776c0 43.055 34.901 77.955 77.953 77.955H628.04c43.051 0 77.955-34.901 77.955-77.955v-93.93l222.94 149.59a19.454 19.454 0 0 0 19.964 0.877 19.47 19.47 0 0 0 10.239-17.15V329.174a19.473 19.473 0 0 0-10.162-17.109z m-281.96 180.688a19.547 19.547 0 0 0-0.4 3.903v112.063c0 1.321 0.14 2.624 0.4 3.894v126.845c0 21.527-17.452 38.98-38.976 38.98H140.818c-21.525 0-38.978-17.452-38.978-38.98V349.68c0-21.525 17.452-38.978 38.978-38.978H628.04c21.523 0 38.976 17.452 38.976 38.978v143.073z m253.146 249.929L705.996 598.48v-91.471l214.166-142.055v377.728z'/>
    </G>
})

export const VoiceCallMessageIcon = createIcon({
    viewBox: '0 0 1024 1024',
    path: <G>
        <Path d='M819.2 512a307.2 307.2 0 0 0-307.2-307.2V153.6a358.4 358.4 0 0 1 358.4 358.4h-51.2z m-307.2-179.2a179.2 179.2 0 0 1 179.2 179.2h51.2A230.4 230.4 0 0 0 512 281.6v51.2zM512 409.6a102.4 102.4 0 0 1 102.4 102.4h-102.4V409.6zM292.1472 187.5968H183.9104c-72.8064 0-124.7232 68.5568-101.5296 137.216 53.1456 157.5424 124.5696 297.0112 216.7808 389.2224 92.16 92.16 231.68 163.6352 389.2224 216.7808 68.608 23.1936 137.216-28.7232 137.216-101.5296v-108.2368c0-24.832-13.4656-47.616-35.1232-59.6992l-94.2592-52.4288-6.7072-3.2256a68.3008 68.3008 0 0 0-57.0368 1.8432l-92.2624 46.08-3.4304-0.9728a271.9232 271.9232 0 0 1-84.6336-42.5984 244.3776 244.3776 0 0 1-49.2544-49.3056 274.2784 274.2784 0 0 1-36.5568-66.9696l-4.4032-12.3904-2.6624-8.8576 46.08-92.1088a68.2496 68.2496 0 0 0-1.3312-63.6416L351.7952 222.72a68.2496 68.2496 0 0 0-59.648-35.1232z m0 51.2c6.1952 0 11.9296 3.3792 14.8992 8.8064l52.224 94.0032a17.0496 17.0496 0 0 1 0.3072 15.872L305.408 465.92l1.6896 8.5504c5.8368 30.1056 22.272 73.472 54.784 116.9408a296.1408 296.1408 0 0 0 59.4944 59.648c43.5712 32.6656 87.04 49.0496 117.248 54.8352l8.4992 1.6384 108.288-54.1184a17.0496 17.0496 0 0 1 15.872 0.3072l94.3104 52.4288c5.4272 3.0208 8.8064 8.704 8.8064 14.8992v108.2368c0 38.4512-34.816 64.8192-69.632 53.0944-150.6816-50.8416-284.0576-119.1936-369.408-204.4928-85.3504-85.3504-153.6-218.7264-204.4928-369.408-11.776-34.816 14.592-69.632 53.0432-69.632h108.2368z'/>
    </G>
})

