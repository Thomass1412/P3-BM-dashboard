import React from "react";


function Logo(color, bColor) {
    return (
        <svg id="Logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 2100 150" fill= {color} >
            <rect className="burger" x="1210.07" y="2.43" width="109.66" height="26.31" fill={bColor}></rect>
            <rect className="burger" x="1210.07" y="60.14" width="109.66" height="26.31" fill={bColor}></rect>
            <rect className="burger" x="1210.07" y="117.86" width="109.66" height="26.31" fill={bColor}></rect>
            <path className="text" d="M1990.24,88.89l22.48-54.27,22.47,54.27Zm67.83,55.27h34.42L2029.12,2.43h-32.4l-63.17,141.73h33.61l12.56-30.37h65.8Z"></path>
            <rect className="text" x="1887.79" y="2.43" width="32.8" height="141.73"></rect>
            <path className="text" d="M1755.37,117.23V29.36h30c28.15,0,46.57,16.8,46.57,43.94s-18.42,43.93-46.57,43.93Zm-32.8,26.93H1787c46.37,0,78.16-27.94,78.16-70.86S1833.33,2.43,1787,2.43h-64.39Z"></path>
            <polygon className="text" points="1621.26 117.84 1621.26 85.04 1687.06 85.04 1687.06 59.53 1621.26 59.53 1621.26 28.75 1695.77 28.75 1695.77 2.43 1588.66 2.43 1588.66 144.16 1698.4 144.16 1698.4 117.84 1621.26 117.84"></polygon>
            <polygon className="text" points="1558.92 144.16 1558.51 2.43 1531.58 2.43 1479.35 90.51 1426.3 2.43 1399.16 2.43 1399.16 144.16 1429.94 144.16 1429.94 60.95 1471.45 129.18 1486.23 129.18 1527.94 59.12 1528.14 144.16 1558.92 144.16"></polygon>
            <polygon className="text" points="1159.23 144.16 1197.5 144.16 1135.34 65.6 1194.06 2.43 1157.62 2.43 1094.85 68.64 1094.85 2.43 1062.25 2.43 1062.25 144.16 1094.85 144.16 1094.85 108.33 1113.88 88.48 1159.23 144.16"></polygon>
            <path className="text" d="M981.67,146.59c40.49,0,60.13-20.24,60.13-43.93,0-52-82.41-34-82.41-60.14,0-8.91,7.5-16.2,26.93-16.2,12.56,0,26.12,3.65,39.28,11.14l10.13-24.91C1022.57,4.25,1004.34,0,986.53,0c-40.3,0-59.74,20-59.74,44.14,0,52.64,82.41,34.42,82.41,61,0,8.7-7.89,15.18-27.33,15.18-17,0-34.83-6.07-46.77-14.78L924,130.19c12.55,9.72,35.23,16.4,57.71,16.4"></path>
            <path className="text" d="M872.77,112.58a52.1,52.1,0,0,1-25.51,6.07c-27.13,0-46-18.83-46-45.35,0-26.93,18.83-45.36,46.37-45.36,14.37,0,26.32,5.06,36.65,16l21-19.44C891.61,8.5,871,0,846.05,0c-45.15,0-78,30.57-78,73.3s32.81,73.29,77.35,73.29c20.25,0,41.71-6.27,57.3-18V71.07h-30Z"></path>
            <polygon className="text" points="713.02 2.43 713.02 88.48 642.56 2.43 615.43 2.43 615.43 144.16 647.82 144.16 647.82 58.11 718.49 144.16 745.42 144.16 745.42 2.43 713.02 2.43"></polygon>
            <rect className="text" x="552.57" y="2.43" width="32.8" height="141.73"></rect>
            <polygon className="text" points="432.3 144.16 536.17 144.16 536.17 117.44 465.1 117.44 465.1 2.43 432.3 2.43 432.3 144.16"></polygon>
            <path className="text" d="M375.21,53.86c0,15.39-10.13,24.7-30.17,24.7H318.31V29.16H345c20,0,30.17,9.11,30.17,24.7M411,144.16,379.25,98.61c18.43-7.9,29.16-23.49,29.16-44.75,0-31.79-23.69-51.43-61.55-51.43H285.51V144.16h32.8V104.68h30.17l27.33,39.48Z"></path>
            <polygon className="text" points="184.27 117.84 184.27 85.04 250.08 85.04 250.08 59.53 184.27 59.53 184.27 28.75 258.78 28.75 258.78 2.43 151.67 2.43 151.67 144.16 261.42 144.16 261.42 117.84 184.27 117.84"></polygon>
            <path className="text" d="M70.87,119.46H32.6v-35H70.87c17,0,26.11,5.67,26.11,17.62,0,12.15-9.1,17.41-26.11,17.41M65.2,27.13c16,0,24.7,5.47,24.7,16.61s-8.71,16.8-24.7,16.8H32.6V27.13Zm38.87,43.33c11.54-6.07,18.83-17,18.83-31.18,0-22.07-18.22-36.85-53.65-36.85H0V144.16H73.3c37.25,0,56.69-14.17,56.69-38.67,0-17.82-10.12-30-25.92-35"></path>
        </svg>
    );
}
export default Logo;