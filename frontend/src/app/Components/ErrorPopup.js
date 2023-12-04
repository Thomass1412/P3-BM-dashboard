import React from 'react'
import './ErrorPopup.scss'

function ErrorPopup(props) {
  return (props.trigger) ? (
    <div className="alert danger-alert">
      <h3>hello22</h3>
      <a class="close">&times;</a>
    </div>
  ) : null;
}

export default ErrorPopup