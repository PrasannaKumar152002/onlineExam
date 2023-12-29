import React from "react";

export default function TrueFalse(props) {
  return (
    <>
      <div className="container">
        <div className="row">
          <div className="col-6 row mt-3 d-flex align-items-center justify-content-center">
            <label
              htmlFor="optionA"
              className="col-sm-2 mt-3"
              style={{ fontWeight: "bolder" }}
            >
              Option A
            </label>
            <div className="col-auto">
              <textarea
                type="text"
                name="optionA"
                className="form-control mx-sm-5"
                defaultValue={
                  props.changedoptionA ? props.changedoptionA : props.optionA
                }
                onChange={(value) => props.changeOptionAHandler(value)}
              ></textarea>
              <div className="invalid-feedback mx-sm-5" id="optionaerr">
                Please Enter Option A
              </div>
            </div>
          </div>
          <div className="col-6 row mt-3 ms-3 d-flex align-items-center justify-content-center">
            <label
              htmlFor="optionB"
              className="col-sm-2 mt-2"
              style={{ fontWeight: "bolder" }}
            >
              Option B
            </label>
            <div className="col-auto">
              <textarea
                type="text"
                name="optionB"
                className="form-control mx-sm-5"
                defaultValue={
                  props.changedoptionB ? props.changedoptionB : props.optionB
                }
                onChange={(value) => props.changeOptionBHandler(value)}
              ></textarea>
              <div className="invalid-feedback mx-sm-5" id="optionberr">
                Please Enter Option B
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
