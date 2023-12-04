import React from 'react'
import './ErrorPopup.css'

function ErrorPopup(props) {
  return (props.trigger) ? (
    <div className="ErrorPopup">
      {props.children}
    </div>
  ) : null;
}

export default ErrorPopup