import { useState, useEffect } from "react";

const [query, setQuery] = useState("");

useEffect(() => {

    const timer = setTimeout(() => {

        if (query)
            searchNews(query);

    }, 500);

    return () => clearTimeout(timer);

}, [query]);