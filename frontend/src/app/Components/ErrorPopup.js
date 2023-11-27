import React from 'react'
import './ErrorPopup.css'

function ErrorPopup(props) {
  return (props.trigger) ? (
    <div className="ErrorPopup">
      <div className="ErrorPopup-inner">
        {props.children}

      </div>
    </div>
  ) : null;
}

export default ErrorPopup