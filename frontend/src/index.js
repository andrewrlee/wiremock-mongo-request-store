import React from 'react';
import ReactDOM from 'react-dom';
import './main.css';

function extractFields(data) {

  const stores = Object.keys(data);
  const fields = new Set();
  const tags = new Set();

  for (var storeIndex in data) {

    var store = data[storeIndex]

    for (let tag in store) {
      tags.add(tag);
      for (let field in store[tag]) {
        fields.add(store[tag][field]);
      }
    }
  }

  return {stores: stores, tags: Array.from(tags), fields: Array.from(fields)}
}

function Select(props) {

  const optionsStart = props.hasEmptyValue
    ? [""]
    : [];

  const options = optionsStart.concat(props.values).map((val, step) => {
    return (<option key={val} value={val}>{val}</option>)
  });

  return (<div className="form-group">
    <label htmlFor="{props.id}">{props.label}:</label>&nbsp;
    <select className="form-control" id={props.id} name={props.id} onChange={e => props.setValue(e.target.name, e.target.value)}>
      {options}
    </select>
  </div>)
}

function Search(props) {

  const reload = (e) => {
    e.preventDefault();
    window.location.reload();
  }

  const search = (e) => {
    e.preventDefault();
    props.search();
  }

  return (<form className="form-inline" onSubmit={search}>
    <div className="col-md-2 col-md-offset-2">
      <Select id="store" label="Store" setValue={props.setValue} values={props.stores}/>
    </div>
    <div className="col-md-2">
      <Select id="tag" label="Tag" hasEmptyValue={true} setValue={props.setValue} values={props.tags}/>
    </div>
    <div className="col-md-4">
      <Select id="field" label="Field" hasEmptyValue={true} setValue={props.setValue} values={props.fields}/>
      <div className="form-group">
        <input id="value" name="value" type="text" className="form-control" onBlur={e => props.setValue(e.target.name, e.target.value)}></input>
      </div>
      <button id="search" type="submit" className="btn btn-default">Search</button>
    </div>
    <div className="col-sm-1 pull-right">
      <button type="submit" className="btn btn-default" onClick={reload}>Clear</button>
    </div>
  </form>);
}

function Requests(props) {

  const entries = (props.results || {
    entries: []
  }).entries || []

  const select = (result) => {
    return e => {
      e.preventDefault();
      props.setValue("selectedItem", result.item);
    }
  }

  var timestamps = entries.map(item => {
    return {
      "id": item.id,
      "display": item.timestamp["$date"] + " (" + item.tags + ")",
      "item": item
    }
  })

  const results = timestamps.map(result => {
    const classes = result.id === (props.selectedItem || {id: ''}).id ? 'request list-group-item active' : 'request list-group-item';
    return (<a className={classes} key={result.id} href="/" onClick={select(result)}>
        {result.display}
      </a>)
  });

  return (<div className="col-sm-3">
    <div className="panel panel-default tall-panel">
      <h4>Requests</h4>
      <div id="requests" className="panel-body">
        <ul className="list-group">
          {results}
        </ul>
        <button onClick={props.loadPrevious}>Previous</button>
        <button onClick={props.loadNext}>Next</button>
      </div>
    </div>
  </div>);
}

function Details(props) {
  const selected = props.selectedItem;

  if (!selected) {
    return <div className="nothing-selected col-md-2 col-md-offset-2">
      <p>No item selected</p>
    </div>
  }

  const tags = selected.tags.join(", ")
  const fields = JSON.stringify(selected.fields, null, 2);
  const entity = selected.request.body
    ? JSON.stringify(JSON.parse(selected.request.body), null, 2)
    : "";

  return (<div className="col-sm-8 panel panel-default tall-panel">
    <p>
      <strong>Tags:</strong>
      <span>{tags}</span>
    </p>
    <p>
      <strong>Timestamp:
      </strong>
      <span>{selected.timestamp["$date"]}</span>
    </p>
    <p>
      <strong>Fields:</strong>
      <span>{fields}</span>
    </p>
    <div id='details' className="panel-body">
      <div className="col-sm-6">
        <h4>Request:</h4>
        <p>
          <strong>URL:
          </strong>
          <span>{decodeURIComponent(selected.request.url)}</span>
        </p>
        <p>
          <strong>Method:
          </strong>
          <span>{selected.request.method}</span>
        </p>
        <p>
          <strong>Headers:</strong>
        </p>
        <pre><code>{JSON.stringify(selected.request.headers, null, 2)}</code></pre>
        <p>
          <strong>Cookies:</strong>
        </p>
        <pre><code>{JSON.stringify(selected.request.cookies, null, 2)}</code></pre>

        <p>
          <strong>Body:</strong>
        </p>
        <pre><code>{entity}</code></pre>
        <p>
          <strong>Client IP:
          </strong>
          <span>{selected.request.clientIp}</span>
        </p>

      </div>
      <div id="details-response" className="col-sm-6">
        <h4>Response</h4>
        <p>
          <strong>Status:</strong>
          <span>{selected.response.status}</span>
        </p>
        <p>
          <strong>Body:</strong>
        </p>

        <pre><code id="details-res">{JSON.stringify(selected.response.body, null, 2)}</code></pre>
      </div>
    </div>
  </div>)
}

class Root extends React.Component {
  constructor(props) {
    super(props);
    this.setValue = this.setValue.bind(this);
    this.search = this.search.bind(this);
    this.loadPrevious = this.loadPrevious.bind(this);
    this.loadNext = this.loadNext.bind(this);

    this.state = {
      error: null,
      isLoaded: false,
      stores: [],
      tags: [],
      fields: [],
      tag: "",
      field: "",
      store: ""
    };
  }

  getUrl = function() {
    const store = this.state.store;
    const tag = this.state.tag;

    var url = (tag === "")
      ? '/__admin/store/' + store + '/entries/'
      : '/__admin/store/' + store + '/entries/tag/' + tag;

    const encodeGetParams = p => Object.entries(p).map(kv => kv.map(encodeURIComponent).join("=")).join("&");

    const field = this.state.field;
    const value = this.state.value;

    return (field && value)
      ? url + "?" + encodeGetParams({[field]: value})
      : url;
  }

  loadResults = function(url) {
    fetch(url).then(res => res.json()).then((result) => {
      this.setState({results: result});
    }, (error) => {
      this.setState({isLoaded: true, error});
    });
  }

  search = function() {
    const url = this.getUrl();
    this.loadResults(url);
    if (this.state.tag) {
      var fields = this.state.allFields[this.state.store][this.state.tag];
      this.setState({"fields": fields});
    }
  }

  loadNext = function() {
    const links = (this.state.results || {
      links: []
    }).links
    if (links.next) {
      this.loadResults(links.next);
    }
  }

  loadPrevious = function() {
    const links = (this.state.results || {
      links: []
    }).links
    if (links.previous) {
      this.loadResults(links.previous);
    }
  }

  setValue = function(name, value) {
    this.setState({
      [name]: value
    }, this.search);
  }

  componentDidMount() {
    fetch('/__admin/store/fields').then(res => res.json()).then((result) => {
      const fields = extractFields(result);
      const newState = Object.assign({
        isLoaded: true,
        allFields: result,
        store: fields.stores[0]
      }, fields);
      this.setState(newState);
      this.search();
    }, (error) => {
      this.setState({isLoaded: true, error});
    })
  }

  render() {
    return (<div className="container-fluid">
      <div className="row content">
        <h2>Recorded Requests</h2>
        <Search stores={this.state.stores} tags={this.state.tags} fields={this.state.fields} search={this.search} setValue={this.setValue}/>
      </div>
      <div className="results row">
        <Requests
          results={this.state.results}
          selectedItem={this.state.selectedItem}
          setValue={this.setValue}
          loadNext={this.loadNext}
          loadPrevious={this.loadPrevious}/>
        <Details selectedItem={this.state.selectedItem}/>
      </div>
    </div>);
  }
}

ReactDOM.render(<Root/>, document.getElementById('root'));
